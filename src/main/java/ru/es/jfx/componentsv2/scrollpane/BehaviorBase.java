package ru.es.jfx.componentsv2.scrollpane;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import com.sun.javafx.scene.traversal.Direction;

/**
 * A convenient base class from which all our built-in behaviors extend. The
 * main functionality in BehaviorBase revolves around infrastructure for
 * resolving key events into function calls. The differences between platforms
 * can be subtle, and we attempt to build sufficient infrastructure into
 * BehaviorBase to minimize the amount of code and the complexity of code
 * necessary to support multiple platforms sufficiently well.
 *
 * <p>Although BehaviorBase is typically used as a base class, it is not abstract and
 * several skins instantiate an instance of BehaviorBase directly.</p>
 *
 * <p>BehaviorBase also implements the hooks for focus traversal. This
 * implementation is sufficient for most subclasses of BehaviorBase. The
 * following action names are registered in the keyMap for handling focus
 * traversal. Subclasses which need to invoke focus traversal using non-standard
 * key strokes should map key strokes to these action names:</p>
 * <ul>
 *  <li>TraverseUp</li>
 *  <li>TraverseDown</li>
 *  <li>TraverseLeft</li>
 *  <li>TraverseRight</li>
 *  <li>TraverseNext</li>
 *  <li>TraversePrevious</li>
 * </ul>
 *
 * <p>Note that by convention, action names are camel case with the first letter
 * uppercase, matching class naming conventions.</p>
 */
public class BehaviorBase<C extends Control> {
    /**
     * A static final reference to whether the platform we are on supports touch.
     */
    protected final static boolean IS_TOUCH_SUPPORTED = Platform.isSupported(ConditionalFeature.INPUT_TOUCH);


    /**
     * The Control with which this Behavior is used. This must be specified in
     * the constructor and must not be null.
     */
    private final C control;
    /**
     * Listens to any key events on the Control and responds to them
     */
    private final EventHandler<KeyEvent> keyEventListener = e -> {
        if (!e.isConsumed()) {
            callActionForEvent(e);
        }
    };

    /**
     * Listens to any focus events on the Control and calls protected methods as a result
     */
    private final InvalidationListener focusListener = property -> {
        focusChanged();
    };


    public BehaviorBase(final C control) {
        // Don't need to explicitly check for null because Collections.unmodifiableList
        // will die on null, as will the adding of listeners
        this.control = control;
        control.addEventHandler(KeyEvent.ANY, keyEventListener);
        control.focusedProperty().addListener(focusListener);
    }

    /**
     * Called by a Skin when the Skin is disposed. This method
     * allows a Behavior to implement any logic necessary to clean up itself after
     * the Behavior is no longer needed. Calling dispose twice has no effect. This
     * method is intended to be overridden by subclasses, although all subclasses
     * must call super.dispose() or a potential memory leak will result.
     */
    public void dispose() {
        control.removeEventHandler(KeyEvent.ANY, keyEventListener);
        control.focusedProperty().removeListener(focusListener);
    }

    /***************************************************************************
     * Implementation of the Behavior "interface"                              *
     *                                                                         *
     * One of the specialized duties of the behavior is to react to key        *
     * events. The behavior breaks the handling of a key event down into a few *
     * distinct stages. First, the BehaviorBase will analyze the key event and *
     * find the String name of a matching action to invoke, if any. If an      *
     * action exists for this event, the name is then fed to                   *
     * callActionForEvent(name), which will then invoke an actual method on    *
     * the behavior that is the implementation of that action.                 *
     *                                                                         *
     * The reason for returning the intermediate action name as a String is    *
     * twofold. First, the matching is done by analyzing a set of key bindings *
     * which are *statically declared* on each behavior class. The fact that   *
     * they are static means that we cannot refer to an actual instance method *
     * to invoke (such as with lambda's). It is also important that these are  *
     * static to reduce the memory footprint of a control (since having        *
     * per-instance key bindings would add a lot to memory footprint). We      *
     * could have used something other than String as the intermediate token,  *
     * however String is useful if we ever want to expose to developers a way  *
     * to alter the action map from a property file or XML file etc.           *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the control associated with this behavior. Even after the BehaviorBase is
     * disposed, this reference will be non-null.
     *
     * @return The control for this Behavior.
     */
    public final C getControl() { return control; }

    /**
     * Invokes the appropriate action for this key event. This is the main entry point where
     * key events are passed when they occur. This method is responsible for invoking
     * matchActionForEvent, callAction, and consuming the event if it was handled by this control.
     *
     * @param e The key event. Must not be null.
     */
    protected void callActionForEvent(KeyEvent e) {
        String action = matchActionForEvent(e);
        if (action != null) {
            callAction(action);
            e.consume();
        }
    }

    /**
     * Given a key event, this method will find the matching action name, or null if there
     * is not one.
     *
     * @param e The key event. Must not be null.
     * @return The name of the action to invoke, or null if there is not one.
     */
    protected String matchActionForEvent(final KeyEvent e) {

        return null;
    }

    /**
     * Called to invoke the action associated with the given name.
     *
     * <p>When a KeyEvent is handled, it is first passed through
     * callActionForEvent which resolves which "action" should be executed
     * based on the key event. This action is indicated by name. This name is
     * then passed to this function which is responsible for invoking the right
     * function based on the name.</p>
     */
    protected void callAction(String name) {

    }

    /***************************************************************************
     * Focus Traversal methods                                                 *
     **************************************************************************/

    /**
     * Called by any of the BehaviorBase traverse methods to actually effect a
     * traversal of the focus. The default behavior of this method is to simply
     * call impl_traverse on the given node, passing the given direction. A
     * subclass may override this method.
     *
     * @param node The node to call impl_traverse on
     * @param dir The direction to traverse
     */
    protected void traverse(final Node node, final Direction dir) {
        //node.impl_traverse(dir);
    }

    /**
     * Calls the focus traversal engine and indicates that traversal should
     * go the next focusTraversable Node above the current one.
     */
    public final void traverseUp() {
        traverse(control, com.sun.javafx.scene.traversal.Direction.UP);
    }

    /**
     * Calls the focus traversal engine and indicates that traversal should
     * go the next focusTraversable Node below the current one.
     */
    public final void traverseDown() {
        traverse(control, com.sun.javafx.scene.traversal.Direction.DOWN);
    }

    /**
     * Calls the focus traversal engine and indicates that traversal should
     * go the next focusTraversable Node left of the current one.
     */
    public final void traverseLeft() {
        traverse(control, com.sun.javafx.scene.traversal.Direction.LEFT);
    }

    /**
     * Calls the focus traversal engine and indicates that traversal should
     * go the next focusTraversable Node right of the current one.
     */
    public final void traverseRight() {
        traverse(control, com.sun.javafx.scene.traversal.Direction.RIGHT);
    }

    /**
     * Calls the focus traversal engine and indicates that traversal should
     * go the next focusTraversable Node in the focus traversal cycle.
     */
    public final void traverseNext() {
        traverse(control, com.sun.javafx.scene.traversal.Direction.NEXT);
    }

    /**
     * Calls the focus traversal engine and indicates that traversal should
     * go the previous focusTraversable Node in the focus traversal cycle.
     */
    public final void traversePrevious() {
        traverse(control, com.sun.javafx.scene.traversal.Direction.PREVIOUS);
    }

    /***************************************************************************
     * Event handler methods.                                                  *
     *                                                                         *
     * I'm not sure why only mouse events are here. What about drag and        *
     * drop events for instance? What about touch events? What about the       *
     * other mouse events? It does seem like these need to be here, because    *
     * for example mouse interaction logic might differ from platform to       *
     * platform, and the Behavior is supposed to implement all the user        *
     * interaction logic (not just key handling). So it seems like             *
     * BehaviorBase should have methods for handling all forms of input events,*
     * and not just these four mouse events.                                   *
     **************************************************************************/

    /**
     * Called whenever the focus on the control has changed. This method is
     * intended to be overridden by subclasses that are interested in focus
     * change events.
     */
    protected void focusChanged() { }

    /**
     * Invoked by a Skin when the body of the control has been pressed by
     * the mouse. Subclasses should be sure to call super unless they intend
     * to disable any built-in support.
     *
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e) { }

    /**
     * Invoked by a Skin when the body of the control has been dragged by
     * the mouse. Subclasses should be sure to call super unless they intend
     * to disable any built-in support (for example, for tooltips).
     *
     * @param e the mouse event
     */
    public void mouseDragged(MouseEvent e) { }

    /**
     * Invoked by a Skin when the body of the control has been released by
     * the mouse. Subclasses should be sure to call super unless they intend
     * to disable any built-in support (for example, for tooltips).
     *
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e) { }

    /**
     * Invoked by a Skin when the body of the control has been entered by
     * the mouse. Subclasses should be sure to call super unless they intend
     * to disable any built-in support.
     *
     * @param e the mouse event
     */
    public void mouseEntered(MouseEvent e) { }

    /**
     * Invoked by a Skin when the body of the control has been exited by
     * the mouse. Subclasses should be sure to call super unless they intend
     * to disable any built-in support.
     *
     * @param e the mouse event
     */
    public void mouseExited(MouseEvent e) { }

    /**
     * Invoked by a Skin when the control has had its context menu requested,
     * most commonly by right-clicking on the control. Subclasses should be sure
     * to call super unless they intend to disable any built-in support.
     *
     * @param e the context menu event
     */
    public void contextMenuRequested(ContextMenuEvent e) { }
}
