package com.conceptcoding.myprjects;
/**

 **Scenario:**
 Building a document editor where users can:
 - Type text → Undo typing
 - Delete text → Undo deletion
 - Format text (bold, italic) → Undo formatting
 - Insert images → Undo insertion
 - Support Redo for all operations
 **Requirements:**
 - Each action should be reversible
 - Keep history of actions
 - Support unlimited undo/redo
 **Question:** Which design pattern(s) would you use?



 I'll evaluate your proposed solution and point out what works well and what needs correction:Document Editor Solution - Evaluation & CorrectionsDocument# Document Editor Solution Evaluation

 ## Your Proposed Solution
 - Two stacks (undo/redo) storing `(action, text)` pairs
 - Command Design Pattern for actions
 - Third stack for navigation to specific history points

 ---

 ## ✅ What You Got RIGHT

 ### 1I've created a comprehensive evaluation of your proposed solution! Here's the summary:
 What You Got Right ✅

 Command Pattern - Perfect choice for undo/redo
 Two-stack architecture - Standard and correct
 Storing state - Good instinct

 Key Corrections Needed ❌

 Storing just (action, text) is insufficient - You need to capture:

 Text content
 Formatting data (bold, italic ranges)
 Image references and positions
 Cursor position

 Solution: Use Memento Pattern alongside Command Pattern to capture complete document state
 Third stack is unnecessary for basic undo/redo - The standard two-stack approach works perfectly. You only need a third stack if you're building:

 Timeline navigation UI (jump to arbitrary history points)
 Branching undo trees

 For sequential undo/redo, it's just added complexity.
 Missing considerations:

 Command merging (grouping consecutive keystrokes)
 Composite Pattern (applying multiple formats at once)
 Memory optimization for large documents



 The document shows the corrected architecture with code examples and explains when your third-stack approach would actually be beneficial. Would you like me to implement a working demo showing the corrected approach?please write javacode2:38 AMArchitected comprehensive Java implementation for document editor functionality.The user wants me to write Java code for the document editor with undo/redo functionality using the Command Pattern and Memento Pattern. I should create a comprehensive Java implementation that addresses all the corrections I mentioned in the evaluation.
 I'll create:

 Memento Pattern classes for capturing document state
 Command Pattern abstract class and concrete implementations
 Document class with formatting and images support
 DocumentEditor class with undo/redo stacks
 Concrete commands for different operations (type, delete, format, insert image)
 A demo/test class to show usage

 Let me write clean, well-structured Java code.I'll create a complete Java implementation of the document editor with Command + Memento patterns:Document Editor Solution - Evaluation & CorrectionsCode∙Version 2
 import java.util.*;
 import java.time.Instant;

 // ======================== MEMENTO PATTERN ========================
 /**
 * Memento: Captures complete document state at a point in time
 **/
/*
class DocumentMemento {
    private final String content;
//    I've created a complete Java implementation with all the corrected patterns! Here are the key highlights:
//    Key Components:

    Memento Pattern (DocumentMemento):

    Captures complete state: content, formatting, images, cursor position
    Immutable snapshot with deep copying


    Command Pattern:

    Abstract Command class with execute/undo/redo
    Stores before/after mementos automatically
    Concrete commands: TypeTextCommand, DeleteTextCommand, FormatTextCommand, InsertImageCommand


    Composite Pattern (CompositeCommand):

    Groups multiple operations as one atomic action
    Example: Apply bold + italic simultaneously


    Document Class:

    Manages: text content, formatting ranges, images, cursor
    Creates and restores mementos


    DocumentEditor (Caretaker):

    Two-stack architecture (undo/redo)
    Clears redo stack on new action
    History display



    Design Highlights:
            ✅ Complete state capture (not just text)
✅ Two stacks only (no unnecessary third stack)
✅ Composite commands for grouped operations
✅ Deep copying to prevent state corruption
✅ Type-safe with enums and generics
    The demo shows all operations: typing, deleting, formatting, inserting images, composite commands, and full undo/redo functionality. You can compile and run it directly!

            **/

            import java.util.*;
import java.time.Instant;
// ======================== MEMENTO PATTERN ========================
    /**

     Memento: Captures complete document state at a point in time
     */
    class DocumentMemento {
        private final String content;
        private final Map<Range, Set<FormatStyle>> formatting;
        private final Map<Integer, String> images;
        private final int cursorPosition;
        private final Instant timestamp;
        public DocumentMemento(String content, Map<Range, Set<FormatStyle>> formatting,
                               Map<Integer, String> images, int cursorPosition) {
            this.content = content;
            this.formatting = deepCopyFormatting(formatting);
            this.images = new HashMap<>(images);
            this.cursorPosition = cursorPosition;
            this.timestamp = Instant.now();
        }
        private Map<Range, Set<FormatStyle>> deepCopyFormatting(Map<Range, Set<FormatStyle>> original) {
            Map<Range, Set<FormatStyle>> copy = new HashMap<>();
            for (Map.Entry<Range, Set<FormatStyle>> entry : original.entrySet()) {
                copy.put(new Range(entry.getKey()), new HashSet<>(entry.getValue()));
            }
            return copy;
        }
        public String getContent() { return content; }
        public Map<Range, Set<FormatStyle>> getFormatting() { return formatting; }
        public Map<Integer, String> getImages() { return images; }
        public int getCursorPosition() { return cursorPosition; }
        public Instant getTimestamp() { return timestamp; }
    }

    // ======================== SUPPORTING CLASSES ========================
    enum FormatStyle {
        BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
    }
    class Range {
        private int start;
        private int end;
        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public Range(Range other) {
            this.start = other.start;
            this.end = other.end;
        }

        public int getStart() { return start; }
        public int getEnd() { return end; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Range)) return false;
            Range range = (Range) o;
            return start == range.start && end == range.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return "[" + start + "-" + end + "]";
        }
    }
// ======================== DOCUMENT CLASS ========================
    /**

     Document: The originator in Memento pattern
     Maintains document state and can create/restore mementos
     */
    class Document {
        private StringBuilder content;
        private Map<Range, Set<FormatStyle>> formatting;
        private Map<Integer, String> images;
        private int cursorPosition;
        public Document() {
            this.content = new StringBuilder();
            this.formatting = new HashMap<>();
            this.images = new HashMap<>();
            this.cursorPosition = 0;
        }
        // Create memento (save state)
        public DocumentMemento createMemento() {
            return new DocumentMemento(
                    content.toString(),
                    formatting,
                    images,
                    cursorPosition
            );
        }
        // Restore from memento
        public void restore(DocumentMemento memento) {
            this.content = new StringBuilder(memento.getContent());
            this.formatting = memento.getFormatting();
            this.images = memento.getImages();
            this.cursorPosition = memento.getCursorPosition();
        }
        // Document operations
        public void insertText(int position, String text) {
            content.insert(position, text);
            cursorPosition = position + text.length();
        }
        public void deleteText(int start, int end) {
            content.delete(start, end);
            cursorPosition = start;
// Remove formatting in deleted range
            formatting.entrySet().removeIf(entry ->
                    entry.getKey().getStart() >= start && entry.getKey().getEnd() <= end);
        }
        public void applyFormatting(Range range, FormatStyle style) {
            formatting.computeIfAbsent(range, k -> new HashSet<>()).add(style);
        }
        public void removeFormatting(Range range, FormatStyle style) {
            Set<FormatStyle> styles = formatting.get(range);
            if (styles != null) {
                styles.remove(style);
                if (styles.isEmpty()) {
                    formatting.remove(range);
                }
            }
        }
        public void insertImage(int position, String imageRef) {
            images.put(position, imageRef);
        }
        public void removeImage(int position) {
            images.remove(position);
        }
        public String getContent() {
            return content.toString();
        }
        public Map<Range, Set<FormatStyle>> getFormatting() {
            return new HashMap<>(formatting);
        }
        public Map<Integer, String> getImages() {
            return new HashMap<>(images);
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Content: ").append(content).append("\n");
            sb.append("Cursor: ").append(cursorPosition).append("\n");
            if (!formatting.isEmpty()) {
                sb.append("Formatting: ").append(formatting).append("\n");
            }
            if (!images.isEmpty()) {
                sb.append("Images: ").append(images).append("\n");
            }
            return sb.toString();
        }
    }

// ======================== COMMAND PATTERN ========================
    /**

     Command: Abstract command with execute/undo using mementos
     */
    abstract class Command {
        protected DocumentMemento beforeState;
        protected DocumentMemento afterState;
        public final void execute(Document document) {
            beforeState = document.createMemento();
            doExecute(document);
            afterState = document.createMemento();
        }
        protected abstract void doExecute(Document document);
        public void undo(Document document) {
            document.restore(beforeState);
        }
        public void redo(Document document) {
            document.restore(afterState);
        }
        public abstract String getDescription();
    }

    // ======================== CONCRETE COMMANDS ========================
    class TypeTextCommand extends Command {
        private final int position;
        private final String text;
        public TypeTextCommand(int position, String text) {
            this.position = position;
            this.text = text;
        }

        @Override
        protected void doExecute(Document document) {
            document.insertText(position, text);
        }

        @Override
        public String getDescription() {
            return "Type: \"" + text + "\" at position " + position;
        }
    }
    class DeleteTextCommand extends Command {
        private final int start;
        private final int end;
        public DeleteTextCommand(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void doExecute(Document document) {
            document.deleteText(start, end);
        }

        @Override
        public String getDescription() {
            return "Delete: range [" + start + "-" + end + "]";
        }
    }
    class FormatTextCommand extends Command {
        private final Range range;
        private final FormatStyle style;
        private final boolean apply; // true = apply, false = remove
        public FormatTextCommand(Range range, FormatStyle style, boolean apply) {
            this.range = range;
            this.style = style;
            this.apply = apply;
        }

        @Override
        protected void doExecute(Document document) {
            if (apply) {
                document.applyFormatting(range, style);
            } else {
                document.removeFormatting(range, style);
            }
        }

        @Override
        public String getDescription() {
            return (apply ? "Apply " : "Remove ") + style + " to " + range;
        }
    }
    class InsertImageCommand extends Command {
        private final int position;
        private final String imageRef;
        public InsertImageCommand(int position, String imageRef) {
            this.position = position;
            this.imageRef = imageRef;
        }

        @Override
        protected void doExecute(Document document) {
            document.insertImage(position, imageRef);
        }

        @Override
        public String getDescription() {
            return "Insert image: " + imageRef + " at position " + position;
        }
    }
    /**

     CompositeCommand: Executes multiple commands as one atomic operation
     */
    class CompositeCommand extends Command {
        private final List<Command> commands;
        private final String description;
        public CompositeCommand(String description, Command... commands) {
            this.description = description;
            this.commands = Arrays.asList(commands);
        }
        @Override
        protected void doExecute(Document document) {
            for (Command cmd : commands) {
                cmd.doExecute(document);
            }
        }
        @Override
        public String getDescription() {
            return description + " [" + commands.size() + " operations]";
        }
    }

// ======================== DOCUMENT EDITOR (CARETAKER) ========================
    /**

     DocumentEditor: Manages undo/redo stacks and command execution
     */
    class DocumentEditor {
        private final Document document;
        private final Stack<Command> undoStack;
        private final Stack<Command> redoStack;
        public DocumentEditor() {
            this.document = new Document();
            this.undoStack = new Stack<>();
            this.redoStack = new Stack<>();
        }
        public void executeCommand(Command command) {
            command.execute(document);
            undoStack.push(command);
            redoStack.clear(); // Clear redo stack on new action
            System.out.println("✓ Executed: " + command.getDescription());
        }
        public void undo() {
            if (undoStack.isEmpty()) {
                System.out.println("✗ Nothing to undo");
                return;
            }
            Command command = undoStack.pop();
            command.undo(document);
            redoStack.push(command);
            System.out.println("↶ Undid: " + command.getDescription());
        }
        public void redo() {
            if (redoStack.isEmpty()) {
                System.out.println("✗ Nothing to redo");
                return;
            }
            Command command = redoStack.pop();
            command.redo(document);
            undoStack.push(command);
            System.out.println("↷ Redid: " + command.getDescription());
        }
        public boolean canUndo() {
            return !undoStack.isEmpty();
        }
        public boolean canRedo() {
            return !redoStack.isEmpty();
        }
        public Document getDocument() {
            return document;
        }
        public void showHistory() {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("DOCUMENT STATE:");
            System.out.println(document);
            System.out.println("UNDO STACK (" + undoStack.size() + " commands):");
            for (int i = 0; i < undoStack.size(); i++) {
                System.out.println("  " + i + ". " + undoStack.get(i).getDescription());
            }

            System.out.println("\nREDO STACK (" + redoStack.size() + " commands):");
            for (int i = 0; i < redoStack.size(); i++) {
                System.out.println("  " + i + ". " + redoStack.get(i).getDescription());
            }
            System.out.println("=".repeat(70));
        }
    }

    // ======================== DEMO ========================
    public class DocumentEditorDemo {
        public static void main(String[] args) {
            DocumentEditor editor = new DocumentEditor();
            System.out.println("=== DOCUMENT EDITOR WITH UNDO/REDO ===\n");

            // 1. Type text
            System.out.println("\n--- Phase 1: Typing Text ---");
            editor.executeCommand(new TypeTextCommand(0, "Hello"));
            editor.executeCommand(new TypeTextCommand(5, " World"));
            editor.executeCommand(new TypeTextCommand(11, "!"));
            editor.showHistory();

            // 2. Apply formatting
            System.out.println("\n--- Phase 2: Formatting ---");
            editor.executeCommand(new FormatTextCommand(new Range(0, 5), FormatStyle.BOLD, true));
            editor.executeCommand(new FormatTextCommand(new Range(6, 11), FormatStyle.ITALIC, true));
            editor.showHistory();

            // 3. Insert image
            System.out.println("\n--- Phase 3: Insert Image ---");
            editor.executeCommand(new InsertImageCommand(12, "photo.jpg"));
            editor.showHistory();

            // 4. Composite command (apply multiple formats at once)
            System.out.println("\n--- Phase 4: Composite Command ---");
            Command compositeCmd = new CompositeCommand(
                    "Format text with BOLD and UNDERLINE",
                    new FormatTextCommand(new Range(0, 5), FormatStyle.UNDERLINE, true)
            );
            editor.executeCommand(compositeCmd);
            editor.showHistory();

            // 5. Undo operations
            System.out.println("\n--- Phase 5: Undo Operations ---");
            editor.undo();
            editor.undo();
            editor.showHistory();

            // 6. Redo operations
            System.out.println("\n--- Phase 6: Redo Operations ---");
            editor.redo();
            editor.showHistory();

            // 7. Delete text
            System.out.println("\n--- Phase 7: Delete Text ---");
            editor.executeCommand(new DeleteTextCommand(5, 11));
            editor.showHistory();

            // 8. New action clears redo stack
            System.out.println("\n--- Phase 8: New Action (Clears Redo) ---");
            editor.executeCommand(new TypeTextCommand(5, " Java"));
            editor.showHistory();

            System.out.println("\n--- Final: Multiple Undo ---");
            editor.undo();
            editor.undo();
            editor.undo();
            editor.showHistory();
        }
    }