/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.build.archetype.engine.v2.interpreter;

import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.helidon.build.archetype.engine.v2.archive.Archetype;
import io.helidon.build.archetype.engine.v2.descriptor.ArchetypeDescriptor;

/**
 * Interpret user inputs and produce new steps.
 */
public class Interpreter implements Visitor<ASTNode> {

    private final Archetype archetype;
    private final Map<String, ContextNodeAST> pathToContextNodeMap;
    private final InputResolverVisitor inputResolverVisitor = new InputResolverVisitor();
    private final UserInputVisitor userInputVisitor = new UserInputVisitor();
    private final List<UserInputAST> unresolvedInputs = new ArrayList<>();
    private final Deque<ASTNode> stack = new ArrayDeque<>();
    private final List<Visitor<ASTNode>> additionalVisitors;

    Interpreter(Archetype archetype, List<Visitor<ASTNode>> additionalVisitors) {
        this.archetype = archetype;
        pathToContextNodeMap = new HashMap<>();
        this.additionalVisitors = additionalVisitors;
    }

    Map<String, ContextNodeAST> pathToContextNodeMap() {
        return pathToContextNodeMap;
    }

    Queue<ASTNode> stack() {
        return stack;
    }

    List<UserInputAST> unresolvedInputs() {
        return unresolvedInputs;
    }

    @Override
    public void visit(Visitable v, ASTNode parent) {
        //todo change
//        v.accept(this, parent);
    }

    @Override
    public void visit(Flow v, ASTNode parent) {
        //todo change
//        v.accept(this, parent);
    }

    @Override
    public void visit(XmlDescriptor xmlDescriptor, ASTNode parent) {
        pushToStack(xmlDescriptor);
        acceptAll(xmlDescriptor, parent);
        stack.pop();
    }

    @Override
    public void visit(StepAST input, ASTNode parent) {
        applyAdditionalVisitors(input);
        pushToStack(input);
        acceptAll(input);
        stack.pop();
    }

    @Override
    public void visit(InputAST input, ASTNode parent) {
        applyAdditionalVisitors(input);
        pushToStack(input);
        acceptAll(input);
        stack.pop();
    }

    @Override
    public void visit(InputBooleanAST input, ASTNode parent) {
        applyAdditionalVisitors(input);
        validate(input);
        pushToStack(input);
        boolean result = resolve(input);
        if (!result) {
            InputNodeAST unresolvedUserInputNode = userInputVisitor.visit(input, parent);
            processUnresolvedInput(input, unresolvedUserInputNode);
        }
        acceptAll(input);
        stack.pop();
    }

    @Override
    public void visit(InputEnumAST input, ASTNode parent) {
        applyAdditionalVisitors(input);
        validate(input);
        pushToStack(input);
        boolean result = resolve(input);
        if (!result) {
            InputNodeAST unresolvedUserInputNode = userInputVisitor.visit(input, parent);
            processUnresolvedInput(input, unresolvedUserInputNode);
        }
        acceptAll(input);
        stack.pop();
    }

    @Override
    public void visit(InputListAST input, ASTNode parent) {
        applyAdditionalVisitors(input);
        validate(input);
        pushToStack(input);
        boolean result = resolve(input);
        if (!result) {
            InputNodeAST unresolvedUserInputNode = userInputVisitor.visit(input, parent);
            processUnresolvedInput(input, unresolvedUserInputNode);
        }
        acceptAll(input);
        stack.pop();
    }

    @Override
    public void visit(InputTextAST input, ASTNode parent) {
        applyAdditionalVisitors(input);
        validate(input);
        pushToStack(input);
        boolean result = resolve(input);
        if (!result) {
            InputNodeAST unresolvedUserInputNode = userInputVisitor.visit(input, parent);
            processUnresolvedInput(input, unresolvedUserInputNode);
        }
        stack.pop();
    }

    @Override
    public void visit(ExecAST exec, ASTNode parent) {
        applyAdditionalVisitors(exec);
        if (pushToStack(exec)) {
            ArchetypeDescriptor descriptor = archetype
                    .getDescriptor(resolveScriptPath(exec.location().scriptDirectory(), exec.src()));
            //todo maybe need to change dir
            String currentDir = Paths
                    .get(resolveScriptPath(exec.location().scriptDirectory(), exec.src()))
                    .getParent().toString();
            ASTNode.Location newLocation = ASTNode.Location.builder()
                    .scriptDirectory(currentDir)
                    .currentDirectory(currentDir)
                    .build();
            XmlDescriptor xmlDescriptor = XmlDescriptor.create(descriptor, exec, newLocation);
            exec.help(xmlDescriptor.help());
            exec.children().addAll(xmlDescriptor.children());
            xmlDescriptor.accept(this, exec);
        }
//        ArchetypeDescriptor descriptor = archetype
//                .getDescriptor(resolveScriptPath(exec.location().scriptDirectory(), exec.src()));
//        //todo maybe need to change dir
//        String currentDir = Paths
//                .get(resolveScriptPath(exec.location().scriptDirectory(), exec.src()))
//                .getParent().toString();
//        ASTNode.Location newLocation = ASTNode.Location.builder()
//                .scriptDirectory(currentDir)
//                .currentDirectory(currentDir)
//                .build();
//        XmlDescriptor xmlDescriptor = XmlDescriptor.create(descriptor, exec, newLocation);
//        pushToStack(exec);
//        xmlDescriptor.accept(this, exec);
//        exec.help(xmlDescriptor.help());
//        exec.children().addAll(xmlDescriptor.children());
        stack.pop();
    }

    @Override
    public void visit(SourceAST source, ASTNode parent) {
        applyAdditionalVisitors(source);
        if (pushToStack((source))) {
            ArchetypeDescriptor descriptor = archetype
                    .getDescriptor(resolveScriptPath(source.location().scriptDirectory(), source.source()));
            //todo maybe need to change dir
            String currentDir = Paths
                    .get(resolveScriptPath(source.location().scriptDirectory(), source.source()))
                    .getParent().toString();
            ASTNode.Location newLocation = ASTNode.Location.builder()
                    .scriptDirectory(currentDir)
                    .currentDirectory(source.location().currentDirectory())
                    .build();
            XmlDescriptor xmlDescriptor = XmlDescriptor.create(descriptor, source, newLocation);
            source.help(xmlDescriptor.help());
            source.children().addAll(xmlDescriptor.children());
            xmlDescriptor.accept(this, source);
        }
//        ArchetypeDescriptor descriptor = archetype
//                .getDescriptor(resolveScriptPath(source.location().scriptDirectory(), source.source()));
//        //todo maybe need to change dir
//        String currentDir = Paths
//                .get(resolveScriptPath(source.location().scriptDirectory(), source.source()))
//                .getParent().toString();
//        ASTNode.Location newLocation = ASTNode.Location.builder()
//                .scriptDirectory(currentDir)
//                .currentDirectory(source.location().currentDirectory())
//                .build();
//        XmlDescriptor xmlDescriptor = XmlDescriptor.create(descriptor, source, newLocation);
//        pushToStack(source);
//        xmlDescriptor.accept(this, source);
//        source.help(xmlDescriptor.help());
//        source.children().addAll(xmlDescriptor.children());
        stack.pop();
    }

    @Override
    public void visit(ContextAST context, ASTNode parent) {
        if (context.parent() == null && parent != null) {
            context.parent(parent);
        }
        applyAdditionalVisitors(context);
//        if (context == null) {
//            return;
//        }
        cleanUnresolvedInputs(context);
        acceptAll(context);
    }

    @Override
    public void visit(ContextBooleanAST contextBoolean, ASTNode parent) {
        if (contextBoolean.parent() == null && parent != null) {
            contextBoolean.parent(parent);
        }
        applyAdditionalVisitors(contextBoolean);
        pathToContextNodeMap.putIfAbsent(contextBoolean.path(), contextBoolean);
    }

    @Override
    public void visit(ContextEnumAST contextEnum, ASTNode parent) {
        if (contextEnum.parent() == null && parent != null) {
            contextEnum.parent(parent);
        }
        applyAdditionalVisitors(contextEnum);
        pathToContextNodeMap.putIfAbsent(contextEnum.path(), contextEnum);
    }

    @Override
    public void visit(ContextListAST contextList, ASTNode parent) {
        if (contextList.parent() == null && parent != null) {
            contextList.parent(parent);
        }
        applyAdditionalVisitors(contextList);
        pathToContextNodeMap.putIfAbsent(contextList.path(), contextList);
    }

    @Override
    public void visit(ContextTextAST contextText, ASTNode parent) {
        if (contextText.parent() == null && parent != null) {
            contextText.parent(parent);
        }
        applyAdditionalVisitors(contextText);
        pathToContextNodeMap.putIfAbsent(contextText.path(), contextText);
    }

    @Override
    public void visit(OptionAST input, ASTNode parent) {
        applyAdditionalVisitors(input);
        pushToStack(input);
        acceptAll(input);
        stack.pop();
    }

    @Override
    public void visit(OutputAST output, ASTNode parent) {
        applyAdditionalVisitors(output);
        pushToStack(output);
        acceptAll(output);
        stack.pop();
    }

    @Override
    public void visit(TransformationAST transformation, ASTNode parent) {
    }

    @Override
    public void visit(FileSetsAST fileSets, ASTNode parent) {
    }

    @Override
    public void visit(FileSetAST fileSet, ASTNode parent) {
    }

    @Override
    public void visit(TemplateAST template, ASTNode parent) {
    }

    @Override
    public void visit(TemplatesAST templates, ASTNode parent) {
    }

    @Override
    public void visit(ModelAST model, ASTNode parent) {
    }

    @Override
    public void visit(IfStatement input, ASTNode parent) {
        if (input != stack.peek()) {
            stack.push(input);
        }
        acceptAll(input);
        stack.pop();
    }

    @Override
    public void visit(ModelKeyValueAST value, ASTNode parent) {
    }

    @Override
    public void visit(ValueTypeAST value, ASTNode parent) {
    }

    @Override
    public void visit(ModelKeyListAST list, ASTNode parent) {
    }

    @Override
    public void visit(MapTypeAST map, ASTNode parent) {
    }

    @Override
    public void visit(ListTypeAST list, ASTNode parent) {
    }

    @Override
    public void visit(ModelKeyMapAST map, ASTNode parent) {
    }

    private void acceptAll(ASTNode node) {
        while (node.hasNext()) {
            Visitable visitable = node.next();
            visitable.accept(this, node);
        }
    }

    private void acceptAll(ASTNode node, ASTNode parent) {
        while (node.hasNext()) {
            Visitable visitable = node.next();
            visitable.accept(this, parent);
        }
    }

    private boolean pushToStack(ASTNode node) {
        if (node != stack.peek()) {
            stack.push(node);
            return true;
        }
        return false;
    }

    private boolean resolve(InputNodeAST input) {
        ContextNodeAST contextNodeAST = pathToContextNodeMap.get(input.path());
        if (contextNodeAST != null) {
            input.accept(inputResolverVisitor, contextNodeAST);
        }
        return contextNodeAST != null;
    }

    /**
     * Create unresolvedInput, add it to the {@code unresolvedInputs} and throw {@link WaitForUserInput} to stop the
     * interpreting process.
     *
     * @param input                   initial unresolved {@code InputNodeAST} from the AST tree.
     * @param unresolvedUserInputNode unresolvedUserInputNode that will be send to the user
     */
    private void processUnresolvedInput(InputNodeAST input, InputNodeAST unresolvedUserInputNode) {
        UserInputAST unresolvedInput = UserInputAST.create(input, getParentStep(input));
        unresolvedInput.children().add(unresolvedUserInputNode);
        unresolvedInputs.add(unresolvedInput);
        throw new WaitForUserInput();
    }

    private StepAST getParentStep(ASTNode node) {
        if (node.parent() == null) {
            return (StepAST) node;
        }
        if (node.parent() instanceof StepAST) {
            return (StepAST) node.parent();
        }
        return getParentStep(node.parent());
    }

    private void cleanUnresolvedInputs(ContextAST context) {
        unresolvedInputs.removeIf(input -> context
                .children().stream()
                .anyMatch(contextNote -> ((ContextNodeAST) contextNote).path().equals(input.path())));
    }

    private String resolveScriptPath(String currentDirectory, String scriptSrc) {
        return Paths.get(currentDirectory).resolve(scriptSrc).normalize().toString();
    }

    private void validate(InputNodeAST input) {
        if (input.isOptional() && input.def() == null) {
            throw new InterpreterException(
                    String.format("Input node %s is optional but it does not have a default value", input.path()));
        }
    }

    private void applyAdditionalVisitors(ASTNode node) {
        additionalVisitors.forEach(visitor -> node.accept(visitor, null));
    }

}
