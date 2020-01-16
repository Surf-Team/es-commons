package ru.es.jfx.componentsv2.scrollpane;


import ru.es.jfx.componentsv2.PFScrollBar;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventDispatcher;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.sun.javafx.util.Utils;
import com.sun.javafx.scene.traversal.TraverseListener;
import static com.sun.javafx.scene.control.skin.Utils.*;
import javafx.geometry.Insets;

public class PFScrollPaneSkin extends BehaviorSkinBase<PFScrollPane, PFScrollPaneBehavior> implements TraverseListener {
    /***************************************************************************
     *                                                                         *
     * UI Subcomponents                                                        *
     *                                                                         *
     **************************************************************************/

    private static final double DEFAULT_PREF_SIZE = 100.0;

    private static final double DEFAULT_MIN_SIZE = 36.0;

    private static final double DEFAULT_SB_BREADTH = 12.0;
    private static final double DEFAULT_EMBEDDED_SB_BREADTH = 8.0;

    private static final double PAN_THRESHOLD = 0.5;

    // state from the control

    private Node scrollNode;

    private double nodeWidth;
    private double nodeHeight;
    private boolean nodeSizeInvalid = true;

    private double posX;
    private double posY;

    // working state

    private boolean hsbvis;
    private boolean vsbvis;
    private double hsbHeight;
    private double vsbWidth;

    // substructure

    private StackPane viewRect;
    private StackPane viewContent;
    private double contentWidth;
    private double contentHeight;
    private StackPane corner;
    protected PFScrollBar hScrollBar;
    protected PFScrollBar vScrollBar;

    double pressX;
    double pressY;
    double ohvalue;
    double ovvalue;
    private Cursor saveCursor =  null;
    private boolean dragDetected = false;
    private boolean touchDetected = false;
    private boolean mouseDown = false;

    Rectangle clipRect;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public PFScrollPaneSkin(final PFScrollPane scrollpane)
    {
        super(scrollpane, new PFScrollPaneBehavior(scrollpane));
        initialize();
        // Register listeners
        registerChangeListener(scrollpane.contentProperty(), "NODE");
        registerChangeListener(scrollpane.fitToWidthProperty(), "FIT_TO_WIDTH");
        registerChangeListener(scrollpane.fitToHeightProperty(), "FIT_TO_HEIGHT");
        registerChangeListener(scrollpane.hbarPolicyProperty(), "HBAR_POLICY");
        registerChangeListener(scrollpane.vbarPolicyProperty(), "VBAR_POLICY");
        registerChangeListener(scrollpane.hvalueProperty(), "HVALUE");
        registerChangeListener(scrollpane.hmaxProperty(), "HMAX");
        registerChangeListener(scrollpane.hminProperty(), "HMIN");
        registerChangeListener(scrollpane.vvalueProperty(), "VVALUE");
        registerChangeListener(scrollpane.vmaxProperty(), "VMAX");
        registerChangeListener(scrollpane.vminProperty(), "VMIN");
        registerChangeListener(scrollpane.prefViewportWidthProperty(), "VIEWPORT_SIZE_HINT");
        registerChangeListener(scrollpane.prefViewportHeightProperty(), "VIEWPORT_SIZE_HINT");
        registerChangeListener(scrollpane.minViewportWidthProperty(), "VIEWPORT_SIZE_HINT");
        registerChangeListener(scrollpane.minViewportHeightProperty(), "VIEWPORT_SIZE_HINT");
    }

    private final InvalidationListener nodeListener = new InvalidationListener() {
        @Override public void invalidated(Observable valueModel) {
            if (!nodeSizeInvalid) {
                final Bounds scrollNodeBounds = scrollNode.getLayoutBounds();
                final double scrollNodeWidth = scrollNodeBounds.getWidth();
                final double scrollNodeHeight = scrollNodeBounds.getHeight();


                if (vsbvis != determineVerticalSBVisible() || hsbvis != determineHorizontalSBVisible() ||
                        (scrollNodeWidth != 0.0  && nodeWidth != scrollNodeWidth) ||
                        (scrollNodeHeight != 0.0 && nodeHeight != scrollNodeHeight)) {
                    getSkinnable().requestLayout();
                } else {

                    if (!dragDetected) {
                        updateVerticalSB();
                        updateHorizontalSB();
                    }
                }
            }
        }
    };


    private final ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {
        @Override public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {
            double oldHeight = oldBounds.getHeight();
            double newHeight = newBounds.getHeight();
            if (oldHeight > 0 && oldHeight != newHeight) {
                double oldPositionY = (snapPosition(snappedTopInset() - posY / (vScrollBar.getMax() - vScrollBar.getMin()) * (oldHeight - contentHeight)));
                double newPositionY = (snapPosition(snappedTopInset() - posY / (vScrollBar.getMax() - vScrollBar.getMin()) * (newHeight - contentHeight)));

                double newValueY = (oldPositionY/newPositionY)* vScrollBar.getValue();
                if (newValueY < 0.0) {
                    vScrollBar.setValue(0.0);
                }
                else if (newValueY < 1.0) {
                    vScrollBar.setValue(newValueY);
                }
                else if (newValueY > 1.0) {
                    vScrollBar.setValue(1.0);
                }
            }

            double oldWidth = oldBounds.getWidth();
            double newWidth = newBounds.getWidth();
            if (oldWidth > 0 && oldWidth != newWidth) {
                double oldPositionX = (snapPosition(snappedLeftInset() - posX / (hScrollBar.getMax() - hScrollBar.getMin()) * (oldWidth - contentWidth)));
                double newPositionX = (snapPosition(snappedLeftInset() - posX / (hScrollBar.getMax() - hScrollBar.getMin()) * (newWidth - contentWidth)));

                double newValueX = (oldPositionX/newPositionX)* hScrollBar.getValue();
                if (newValueX < 0.0) {
                    hScrollBar.setValue(0.0);
                }
                else if (newValueX < 1.0) {
                    hScrollBar.setValue(newValueX);
                }
                else if (newValueX > 1.0) {
                    hScrollBar.setValue(1.0);
                }
            }
        }
    };

    private void initialize() {
        // requestLayout calls below should not trigger requestLayout above ScrollPane
//        setManaged(false);

        PFScrollPane control = getSkinnable();
        scrollNode = control.getContent();

        //ParentTraversalEngine traversalEngine = new ParentTraversalEngine(getSkinnable());
        //traversalEngine.addTraverseListener(this);
        //getSkinnable().setImpl_traversalEngine(traversalEngine);

        if (scrollNode != null) {
            scrollNode.layoutBoundsProperty().addListener(nodeListener);
            scrollNode.layoutBoundsProperty().addListener(boundsChangeListener);
        }

        viewRect = new StackPane() {

            @Override
            protected void layoutChildren() {
                viewContent.resize(getWidth(), getHeight());
            }

        };
        // prevent requestLayout requests from within scrollNode from percolating up
        viewRect.setManaged(false);
        viewRect.setCache(false);
        viewRect.getStyleClass().add("viewport");

        clipRect = new Rectangle();
        viewRect.setClip(clipRect);

        hScrollBar = new PFScrollBar();
        vScrollBar = new PFScrollBar();

        hScrollBar.moveableColor.bind(control.moveableColor);
        vScrollBar.moveableColor.bind(control.moveableColor);

        hScrollBar.info.zoomEnabled.setValue(false);
        vScrollBar.info.zoomEnabled.setValue(false);

        vScrollBar.setOrientation(Orientation.VERTICAL);

        /*EventHandler<MouseEvent> barHandler = ev -> {
            getSkinnable().requestFocus();
        };

        hScrollBar.addEventFilter(MouseEvent.MOUSE_PRESSED, barHandler);
        vScrollBar.addEventFilter(MouseEvent.MOUSE_PRESSED, barHandler);*/

        corner = new StackPane();
        corner.getStyleClass().setAll("corner");

        viewContent = new StackPane() {
            @Override public void requestLayout() {
                // if scrollNode requested layout, will want to recompute
                nodeSizeInvalid = true;

                super.requestLayout(); // add as layout root for next layout pass

                // Need to layout the ScrollPane as well in case scrollbars
                // appeared or disappeared.
                PFScrollPaneSkin.this.getSkinnable().requestLayout();
            }
            @Override protected void layoutChildren() {
                if (nodeSizeInvalid) {
                    computeScrollNodeSize(getWidth(),getHeight());
                }
                if (scrollNode != null && scrollNode.isResizable()) {
                    scrollNode.resize(snapSize(nodeWidth), snapSize(nodeHeight));
                    if (vsbvis != determineVerticalSBVisible() || hsbvis != determineHorizontalSBVisible()) {
                        getSkinnable().requestLayout();
                    }
                }
                if (scrollNode != null) {
                    scrollNode.relocate(0,0);
                }
            }
        };
        viewRect.getChildren().add(viewContent);

        if (scrollNode != null) {
            viewContent.getChildren().add(scrollNode);
            viewRect.nodeOrientationProperty().bind(scrollNode.nodeOrientationProperty());
        }

        getChildren().clear();
        getChildren().addAll(viewRect, vScrollBar, hScrollBar);

        InvalidationListener vsbListener = valueModel -> {
            if (!IS_TOUCH_SUPPORTED) {
                posY = Utils.clamp(getSkinnable().getVmin(), vScrollBar.getValue(), getSkinnable().getVmax());
            }
            else {
                posY = vScrollBar.getValue();
            }
            updatePosY();
        };
        vScrollBar.valueProperty().addListener(vsbListener);

        InvalidationListener hsbListener = valueModel -> {
            if (!IS_TOUCH_SUPPORTED) {
                posX = Utils.clamp(getSkinnable().getHmin(), hScrollBar.getValue(), getSkinnable().getHmax());
            }
            else {
                posX = hScrollBar.getValue();
            }
            updatePosX();
        };
        hScrollBar.valueProperty().addListener(hsbListener);

        viewRect.setOnMousePressed(e -> {
            mouseDown = true;
            if (IS_TOUCH_SUPPORTED) {
                startSBReleasedAnimation();
            }
            pressX = e.getX();
            pressY = e.getY();
            ohvalue = hScrollBar.getValue();
            ovvalue = vScrollBar.getValue();
        });


        viewRect.setOnDragDetected(e -> {
            if (IS_TOUCH_SUPPORTED) {
                startSBReleasedAnimation();
            }
            if (getSkinnable().isPannable()) {
                dragDetected = true;
                if (saveCursor == null) {
                    saveCursor = getSkinnable().getCursor();
                    if (saveCursor == null) {
                        saveCursor = Cursor.DEFAULT;
                    }
                    getSkinnable().setCursor(Cursor.MOVE);
                    getSkinnable().requestLayout();
                }
            }
        });

        viewRect.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            mouseDown = false;
            if (dragDetected == true) {
                if (saveCursor != null) {
                    getSkinnable().setCursor(saveCursor);
                    saveCursor = null;
                    getSkinnable().requestLayout();
                }
                dragDetected = false;
            }


            if ((posY > getSkinnable().getVmax() || posY < getSkinnable().getVmin() ||
                    posX > getSkinnable().getHmax() || posX < getSkinnable().getHmin()) && !touchDetected) {
                startContentsToViewport();
            }
        });
        viewRect.setOnMouseDragged(e -> {
            if (IS_TOUCH_SUPPORTED) {
                startSBReleasedAnimation();
            }
            if (getSkinnable().isPannable() || IS_TOUCH_SUPPORTED) {
                double deltaX = pressX - e.getX();
                double deltaY = pressY - e.getY();

                if (hScrollBar.getVisibleAmount() > 0.0 && hScrollBar.getVisibleAmount() < hScrollBar.getMax()) {
                    if (Math.abs(deltaX) > PAN_THRESHOLD) {
                        if (isReverseNodeOrientation()) {
                            deltaX = -deltaX;
                        }
                        double newHVal = (ohvalue + deltaX / (nodeWidth - viewRect.getWidth()) * (hScrollBar.getMax() - hScrollBar.getMin()));
                        if (!IS_TOUCH_SUPPORTED) {
                            if (newHVal > hScrollBar.getMax()) {
                                newHVal = hScrollBar.getMax();
                            }
                            else if (newHVal < hScrollBar.getMin()) {
                                newHVal = hScrollBar.getMin();
                            }
                            hScrollBar.setValue(newHVal);
                        }
                        else {
                            hScrollBar.setValue(newHVal);
                        }
                    }
                }

                if (vScrollBar.getVisibleAmount() > 0.0 && vScrollBar.getVisibleAmount() < vScrollBar.getMax()) {
                    if (Math.abs(deltaY) > PAN_THRESHOLD) {
                        double newVVal = (ovvalue + deltaY / (nodeHeight - viewRect.getHeight()) * (vScrollBar.getMax() - vScrollBar.getMin()));
                        if (!IS_TOUCH_SUPPORTED) {
                            if (newVVal > vScrollBar.getMax()) {
                                newVVal = vScrollBar.getMax();
                            }
                            else if (newVVal < vScrollBar.getMin()) {
                                newVVal = vScrollBar.getMin();
                            }
                            vScrollBar.setValue(newVVal);
                        }
                        else {
                            vScrollBar.setValue(newVVal);
                        }
                    }
                }
            }

            e.consume();
        });



        // block the event from being passed down to children
        final EventDispatcher blockEventDispatcher = (event, tail) -> event;
        // block ScrollEvent from being passed down to scrollbar's skin
        final EventDispatcher oldHsbEventDispatcher = hScrollBar.getEventDispatcher();
        hScrollBar.setEventDispatcher((event, tail) -> {
            if (event.getEventType() == ScrollEvent.SCROLL &&
                    !((ScrollEvent)event).isDirect()) {
                tail = tail.prepend(blockEventDispatcher);
                tail = tail.prepend(oldHsbEventDispatcher);
                return tail.dispatchEvent(event);
            }
            return oldHsbEventDispatcher.dispatchEvent(event, tail);
        });
        // block ScrollEvent from being passed down to scrollbar's skin
        final EventDispatcher oldVsbEventDispatcher = vScrollBar.getEventDispatcher();
        vScrollBar.setEventDispatcher((event, tail) -> {
            if (event.getEventType() == ScrollEvent.SCROLL &&
                    !((ScrollEvent)event).isDirect()) {
                tail = tail.prepend(blockEventDispatcher);
                tail = tail.prepend(oldVsbEventDispatcher);
                return tail.dispatchEvent(event);
            }
            return oldVsbEventDispatcher.dispatchEvent(event, tail);
        });


        viewRect.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (IS_TOUCH_SUPPORTED) {
                startSBReleasedAnimation();
            }

            if (vScrollBar.getVisibleAmount() < vScrollBar.getMax()) {
                double vRange = getSkinnable().getVmax()-getSkinnable().getVmin();
                double vPixelValue;
                if (nodeHeight > 0.0) {
                    vPixelValue = vRange / nodeHeight;
                }
                else {
                    vPixelValue = 0.0;
                }
                double newValue = vScrollBar.getValue()+(-event.getDeltaY())*vPixelValue;
                if (!IS_TOUCH_SUPPORTED) {
                    if ((event.getDeltaY() > 0.0 && vScrollBar.getValue() > vScrollBar.getMin()) ||
                            (event.getDeltaY() < 0.0 && vScrollBar.getValue() < vScrollBar.getMax())) {
                        vScrollBar.setValue(newValue);
                        event.consume();
                    }
                }
                else {
                    if (!(((ScrollEvent)event).isInertia()) || (((ScrollEvent)event).isInertia()) && (contentsToViewTimeline == null || contentsToViewTimeline.getStatus() == Status.STOPPED)) {
                        vScrollBar.setValue(newValue);
                        if ((newValue > vScrollBar.getMax() || newValue < vScrollBar.getMin()) && (!mouseDown && !touchDetected)) {
                            startContentsToViewport();
                        }
                        event.consume();
                    }
                }
            }

            if (hScrollBar.getVisibleAmount() < hScrollBar.getMax()) {
                double hRange = getSkinnable().getHmax()-getSkinnable().getHmin();
                double hPixelValue;
                if (nodeWidth > 0.0) {
                    hPixelValue = hRange / nodeWidth;
                }
                else {
                    hPixelValue = 0.0;
                }

                double newValue = hScrollBar.getValue()+(-event.getDeltaX())*hPixelValue;
                if (!IS_TOUCH_SUPPORTED) {
                    if ((event.getDeltaX() > 0.0 && hScrollBar.getValue() > hScrollBar.getMin()) ||
                            (event.getDeltaX() < 0.0 && hScrollBar.getValue() < hScrollBar.getMax())) {
                        hScrollBar.setValue(newValue);
                        event.consume();
                    }
                }
                else {
                    if (!(((ScrollEvent)event).isInertia()) || (((ScrollEvent)event).isInertia()) && (contentsToViewTimeline == null || contentsToViewTimeline.getStatus() == Status.STOPPED)) {
                        hScrollBar.setValue(newValue);

                        if ((newValue > hScrollBar.getMax() || newValue < hScrollBar.getMin()) && (!mouseDown && !touchDetected)) {
                            startContentsToViewport();
                        }
                        event.consume();
                    }
                }
            }
        });


        getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, e -> {
            touchDetected = true;
            startSBReleasedAnimation();
            e.consume();
        });

        getSkinnable().addEventHandler(TouchEvent.TOUCH_RELEASED, e -> {
            touchDetected = false;
            e.consume();
        });

        // ScrollPanes do not block all MouseEvents by default, unlike most other UI Controls.
        consumeMouseEvents(false);

        // update skin initial state to match control (see RT-35554)
        hScrollBar.setValue(control.getHvalue());
        vScrollBar.setValue(control.getVvalue());
    }


    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("NODE".equals(p)) {
            if (scrollNode != getSkinnable().getContent()) {
                if (scrollNode != null) {
                    scrollNode.layoutBoundsProperty().removeListener(nodeListener);
                    scrollNode.layoutBoundsProperty().removeListener(boundsChangeListener);
                    viewContent.getChildren().remove(scrollNode);
                }
                scrollNode = getSkinnable().getContent();
                if (scrollNode != null) {
                    nodeWidth = snapSize(scrollNode.getLayoutBounds().getWidth());
                    nodeHeight = snapSize(scrollNode.getLayoutBounds().getHeight());
                    viewContent.getChildren().setAll(scrollNode);
                    scrollNode.layoutBoundsProperty().addListener(nodeListener);
                    scrollNode.layoutBoundsProperty().addListener(boundsChangeListener);
                }
            }
            getSkinnable().requestLayout();
        } else if ("FIT_TO_WIDTH".equals(p) || "FIT_TO_HEIGHT".equals(p)) {
            getSkinnable().requestLayout();
            viewRect.requestLayout();
        } else if ("HBAR_POLICY".equals(p) || "VBAR_POLICY".equals(p)) {
            // change might affect pref size, so requestLayout on control
            getSkinnable().requestLayout();
        } else if ("HVALUE".equals(p)) {
            hScrollBar.setValue(getSkinnable().getHvalue());
        } else if ("HMAX".equals(p)) {
            hScrollBar.setMax(getSkinnable().getHmax());
        } else if ("HMIN".equals(p)) {
            hScrollBar.setMin(getSkinnable().getHmin());
        } else if ("VVALUE".equals(p)) {
            vScrollBar.setValue(getSkinnable().getVvalue());
        } else if ("VMAX".equals(p)) {
            vScrollBar.setMax(getSkinnable().getVmax());
        } else if ("VMIN".equals(p)) {
            vScrollBar.setMin(getSkinnable().getVmin());
        } else if ("VIEWPORT_SIZE_HINT".equals(p)) {
            // change affects pref size, so requestLayout on control
            getSkinnable().requestLayout();
        }
    }

    void scrollBoundsIntoView(Bounds b) {
        double dx = 0.0;
        double dy = 0.0;
        if (b.getMaxX() > contentWidth) {
            dx = b.getMinX() - snappedLeftInset();
        }
        if (b.getMinX() < snappedLeftInset()) {
            dx = b.getMaxX() - contentWidth - snappedLeftInset();
        }
        if (b.getMaxY() > snappedTopInset() + contentHeight) {
            dy = b.getMinY() - snappedTopInset();
        }
        if (b.getMinY() < snappedTopInset()) {
            dy = b.getMaxY() - contentHeight - snappedTopInset();
        }
        // We want to move contentPanel's layoutX,Y by (dx,dy).
        // But to do this we have to set the scrollbars' values appropriately.

        if (dx != 0) {
            double sdx = dx * (hScrollBar.getMax() - hScrollBar.getMin()) / (nodeWidth - contentWidth);
            // Adjust back for some amount so that the Node border is not too close to view border
            sdx += -1 * Math.signum(sdx) * hScrollBar.getUnitIncrement() / 5; // This accounts to 2% of view width
            hScrollBar.setValue(hScrollBar.getValue() + sdx);
            getSkinnable().requestLayout();
        }
        if (dy != 0) {
            double sdy = dy * (vScrollBar.getMax() - vScrollBar.getMin()) / (nodeHeight - contentHeight);
            // Adjust back for some amount so that the Node border is not too close to view border
            sdy += -1 * Math.signum(sdy) * vScrollBar.getUnitIncrement() / 5; // This accounts to 2% of view height
            vScrollBar.setValue(vScrollBar.getValue() + sdy);
            getSkinnable().requestLayout();
        }

    }
    @Override
    public void onTraverse(Node n, Bounds b) {
        scrollBoundsIntoView(b);
    }
    public void hsbIncrement() {
        if (hScrollBar != null) hScrollBar.increment();
    }
    public void hsbDecrement() {
        if (hScrollBar != null) hScrollBar.decrement();
    }

    // TODO: add page increment and decrement
    public void hsbPageIncrement() {
        if (hScrollBar != null) hScrollBar.increment();
    }
    // TODO: add page increment and decrement
    public void hsbPageDecrement() {
        if (hScrollBar != null) hScrollBar.decrement();
    }

    public void vsbIncrement() {
        if (vScrollBar != null) vScrollBar.increment();
    }
    public void vsbDecrement() {
        if (vScrollBar != null) vScrollBar.decrement();
    }

    // TODO: add page increment and decrement
    public void vsbPageIncrement() {
        if (vScrollBar != null) vScrollBar.increment();
    }
    // TODO: add page increment and decrement
    public void vsbPageDecrement() {
        if (vScrollBar != null) vScrollBar.decrement();
    }


    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        final PFScrollPane sp = getSkinnable();

        double vsbWidth = computeVsbSizeHint(sp);
        double minWidth = vsbWidth + snappedLeftInset() + snappedRightInset();

        if (sp.getPrefViewportWidth() > 0) {
            return (sp.getPrefViewportWidth() + minWidth);
        }
        else if (sp.getContent() != null) {
            return (sp.getContent().prefWidth(height) + minWidth);
        }
        else {
            return Math.max(minWidth, DEFAULT_PREF_SIZE);
        }
    }

    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        final PFScrollPane sp = getSkinnable();

        double hsbHeight = computeHsbSizeHint(sp);
        double minHeight = hsbHeight + snappedTopInset() + snappedBottomInset();

        if (sp.getPrefViewportHeight() > 0) {
            return (sp.getPrefViewportHeight() + minHeight);
        }
        else if (sp.getContent() != null) {
            return (sp.getContent().prefHeight(width) + minHeight);
        }
        else {
            return Math.max(minHeight, DEFAULT_PREF_SIZE);
        }
    }

    @Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        final PFScrollPane sp = getSkinnable();

        double vsbWidth = computeVsbSizeHint(sp);
        double minWidth = vsbWidth + snappedLeftInset() + snappedRightInset();

        if (sp.getMinViewportWidth() > 0) {
            return (sp.getMinViewportWidth() + minWidth);
        } else {
            double w = corner.minWidth(-1);
            return (w > 0) ? (3 * w) : (DEFAULT_MIN_SIZE);
        }

    }

    @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        final PFScrollPane sp = getSkinnable();

        double hsbHeight = computeHsbSizeHint(sp);
        double minHeight = hsbHeight + snappedTopInset() + snappedBottomInset();

        if (sp.getMinViewportHeight() > 0) {
            return (sp.getMinViewportHeight() + minHeight);
        } else {
            double h = corner.minHeight(-1);
            return (h > 0) ? (3 * h) : (DEFAULT_MIN_SIZE);
        }
    }

    private double computeHsbSizeHint(PFScrollPane sp) {
        return ((sp.getHbarPolicy() == ScrollPane.ScrollBarPolicy.ALWAYS) ||
                (sp.getHbarPolicy() == ScrollPane.ScrollBarPolicy.AS_NEEDED && (sp.getPrefViewportHeight() > 0 || sp.getMinViewportHeight() > 0)))
                ? hScrollBar.prefHeight(ScrollBar.USE_COMPUTED_SIZE)
                : 0;
    }

    private double computeVsbSizeHint(PFScrollPane sp) {
        return ((sp.getVbarPolicy() == ScrollPane.ScrollBarPolicy.ALWAYS) ||
                (sp.getVbarPolicy() == ScrollPane.ScrollBarPolicy.AS_NEEDED && (sp.getPrefViewportWidth() > 0
                        || sp.getMinViewportWidth() > 0)))
                ? vScrollBar.prefWidth(ScrollBar.USE_COMPUTED_SIZE)
                : 0;
    }

    @Override protected void layoutChildren(final double x, final double y,
                                            final double w, final double h) {
        final PFScrollPane control = getSkinnable();
        final Insets padding = control.getPadding();
        final double rightPadding = snapSize(padding.getRight());
        final double leftPadding = snapSize(padding.getLeft());
        final double topPadding = snapSize(padding.getTop());
        final double bottomPadding = snapSize(padding.getBottom());

        vScrollBar.setMin(control.getVmin());
        vScrollBar.setMax(control.getVmax());

        //should only do this on css setup
        hScrollBar.setMin(control.getHmin());
        hScrollBar.setMax(control.getHmax());

        contentWidth = w;
        contentHeight = h;

        double hsbWidth = 0;
        double vsbHeight = 0;

        computeScrollNodeSize(contentWidth, contentHeight);
        computeScrollBarSize();

        boolean transparantScrolls = true;

        for (int i = 0; i < 2; ++i) {
            vsbvis = determineVerticalSBVisible();
            hsbvis = determineHorizontalSBVisible();

            if (vsbvis && !IS_TOUCH_SUPPORTED) {
                if (!transparantScrolls)
                    contentWidth = w - vsbWidth;
                else
                    contentWidth = w;
            }
            hsbWidth = w + leftPadding + rightPadding - (vsbvis ? vsbWidth : 0);
            if (hsbvis && !IS_TOUCH_SUPPORTED) {
                if (!transparantScrolls)
                    contentHeight = h - hsbHeight;
                else
                    contentHeight = h;
            }
            vsbHeight = h + topPadding + bottomPadding - (hsbvis ? hsbHeight : 0);
        }


        if (scrollNode != null && scrollNode.isResizable()) {
            // maybe adjust size now that scrollbars may take up space
            if (vsbvis && hsbvis) {
                // adjust just once to accommodate
                computeScrollNodeSize(contentWidth, contentHeight);

            } else if (hsbvis && !vsbvis) {
                computeScrollNodeSize(contentWidth, contentHeight);
                vsbvis = determineVerticalSBVisible();
                if (vsbvis) {
                    // now both are visible
                    contentWidth -= vsbWidth;
                    hsbWidth -= vsbWidth;
                    computeScrollNodeSize(contentWidth, contentHeight);
                }
            } else if (vsbvis && !hsbvis) {
                computeScrollNodeSize(contentWidth, contentHeight);
                hsbvis = determineHorizontalSBVisible();
                if (hsbvis) {
                    // now both are visible
                    contentHeight -= hsbHeight;
                    vsbHeight -= hsbHeight;
                    computeScrollNodeSize(contentWidth, contentHeight);
                }
            }
        }

        // figure out the content area that is to be filled
        double cx = snappedLeftInset() - leftPadding;
        double cy = snappedTopInset() - topPadding;

        vScrollBar.setVisible(vsbvis);
        if (vsbvis) {
            vScrollBar.resizeRelocate(snappedLeftInset() + w - vsbWidth + (rightPadding < 1 ? 0 : rightPadding - 1) ,
                    cy, vsbWidth, vsbHeight);
        }
        updateVerticalSB();

        hScrollBar.setVisible(hsbvis);
        if (hsbvis) {

            hScrollBar.resizeRelocate(cx, snappedTopInset() + h - hsbHeight + (bottomPadding < 1 ? 0 : bottomPadding - 1),
                    hsbWidth, hsbHeight);
        }
        updateHorizontalSB();

        viewRect.resizeRelocate(snappedLeftInset(), snappedTopInset(), snapSize(contentWidth), snapSize(contentHeight));
        resetClip();

        if (vsbvis && hsbvis) {
            corner.setVisible(true);
            double cornerWidth = vsbWidth;
            double cornerHeight = hsbHeight;
            corner.resizeRelocate(snapPosition(vScrollBar.getLayoutX()), snapPosition(hScrollBar.getLayoutY()), snapSize(cornerWidth), snapSize(cornerHeight));
        } else {
            corner.setVisible(false);
        }
        control.setViewportBounds(new BoundingBox(snapPosition(viewContent.getLayoutX()), snapPosition(viewContent.getLayoutY()), snapSize(contentWidth), snapSize(contentHeight)));
    }

    private void computeScrollNodeSize(double contentWidth, double contentHeight) {
        if (scrollNode != null) {
            if (scrollNode.isResizable()) {
                PFScrollPane control = getSkinnable();
                Orientation bias = scrollNode.getContentBias();
                if (bias == null) {
                    nodeWidth = snapSize(boundedSize(control.isFitToWidth()? contentWidth : scrollNode.prefWidth(-1),
                            scrollNode.minWidth(-1),scrollNode.maxWidth(-1)));
                    nodeHeight = snapSize(boundedSize(control.isFitToHeight()? contentHeight : scrollNode.prefHeight(-1),
                            scrollNode.minHeight(-1), scrollNode.maxHeight(-1)));

                } else if (bias == Orientation.HORIZONTAL) {
                    nodeWidth = snapSize(boundedSize(control.isFitToWidth()? contentWidth : scrollNode.prefWidth(-1),
                            scrollNode.minWidth(-1),scrollNode.maxWidth(-1)));
                    nodeHeight = snapSize(boundedSize(control.isFitToHeight()? contentHeight : scrollNode.prefHeight(nodeWidth),
                            scrollNode.minHeight(nodeWidth),scrollNode.maxHeight(nodeWidth)));

                } else { // bias == VERTICAL
                    nodeHeight = snapSize(boundedSize(control.isFitToHeight()? contentHeight : scrollNode.prefHeight(-1),
                            scrollNode.minHeight(-1), scrollNode.maxHeight(-1)));
                    nodeWidth = snapSize(boundedSize(control.isFitToWidth()? contentWidth : scrollNode.prefWidth(nodeHeight),
                            scrollNode.minWidth(nodeHeight),scrollNode.maxWidth(nodeHeight)));
                }

            } else {
                nodeWidth = snapSize(scrollNode.getLayoutBounds().getWidth());
                nodeHeight = snapSize(scrollNode.getLayoutBounds().getHeight());
            }
            nodeSizeInvalid = false;
        }
    }

    private boolean isReverseNodeOrientation() {
        return (scrollNode != null &&
                getSkinnable().getEffectiveNodeOrientation() !=
                        scrollNode.getEffectiveNodeOrientation());
    }

    private boolean determineHorizontalSBVisible() {
        final PFScrollPane sp = getSkinnable();

        if (IS_TOUCH_SUPPORTED) {
            return (tempVisibility && (nodeWidth > contentWidth));
        }
        else {
            // RT-17395: ScrollBarPolicy might be null. If so, treat it as "AS_NEEDED", which is the default
            ScrollPane.ScrollBarPolicy hbarPolicy = sp.getHbarPolicy();
            return (ScrollPane.ScrollBarPolicy.NEVER == hbarPolicy) ? false :
                    ((ScrollPane.ScrollBarPolicy.ALWAYS == hbarPolicy) ? true :
                            ((sp.isFitToWidth() && scrollNode != null ? scrollNode.isResizable() : false) ?
                                    (nodeWidth > contentWidth && scrollNode.minWidth(-1) > contentWidth) : (nodeWidth > contentWidth)));
        }
    }

    private boolean determineVerticalSBVisible() {
        final PFScrollPane sp = getSkinnable();

        if (IS_TOUCH_SUPPORTED) {
            return (tempVisibility && (nodeHeight > contentHeight));
        }
        else {
            // RT-17395: ScrollBarPolicy might be null. If so, treat it as "AS_NEEDED", which is the default
            ScrollPane.ScrollBarPolicy vbarPolicy = sp.getVbarPolicy();
            return (ScrollPane.ScrollBarPolicy.NEVER == vbarPolicy) ? false :
                    ((ScrollPane.ScrollBarPolicy.ALWAYS == vbarPolicy) ? true :
                            ((sp.isFitToHeight() && scrollNode != null ? scrollNode.isResizable() : false) ?
                                    (nodeHeight > contentHeight && scrollNode.minHeight(-1) > contentHeight) : (nodeHeight > contentHeight)));
        }
    }

    private void computeScrollBarSize() {
        vsbWidth = snapSize(vScrollBar.prefWidth(-1));
        if (vsbWidth == 0) {
            //            println("*** WARNING ScrollPaneSkin: can't get scroll bar width, using {DEFAULT_SB_BREADTH}");
            if (IS_TOUCH_SUPPORTED) {
                vsbWidth = DEFAULT_EMBEDDED_SB_BREADTH;
            }
            else {
                vsbWidth = DEFAULT_SB_BREADTH;
            }
        }
        hsbHeight = snapSize(hScrollBar.prefHeight(-1));
        if (hsbHeight == 0) {
            //            println("*** WARNING ScrollPaneSkin: can't get scroll bar height, using {DEFAULT_SB_BREADTH}");
            if (IS_TOUCH_SUPPORTED) {
                hsbHeight = DEFAULT_EMBEDDED_SB_BREADTH;
            }
            else {
                hsbHeight = DEFAULT_SB_BREADTH;
            }
        }
    }

    private void updateHorizontalSB() {
        double contentRatio = nodeWidth * (hScrollBar.getMax() - hScrollBar.getMin());
        if (contentRatio > 0.0) {
            hScrollBar.setVisibleAmount(contentWidth / contentRatio);
            hScrollBar.setBlockIncrement(0.9 * hScrollBar.getVisibleAmount());
            hScrollBar.setUnitIncrement(0.1 * hScrollBar.getVisibleAmount());
        }
        else {
            hScrollBar.setVisibleAmount(0.0);
            hScrollBar.setBlockIncrement(0.0);
            hScrollBar.setUnitIncrement(0.0);
        }

        if (hScrollBar.isVisible()) {
            updatePosX();
        } else {
            if (nodeWidth > contentWidth) {
                updatePosX();
            } else {
                viewContent.setLayoutX(0);
            }
        }
    }

    private void updateVerticalSB() {
        double contentRatio = nodeHeight * (vScrollBar.getMax() - vScrollBar.getMin());
        if (contentRatio > 0.0) {
            vScrollBar.setVisibleAmount(contentHeight / contentRatio);
            vScrollBar.setBlockIncrement(0.9 * vScrollBar.getVisibleAmount());
            vScrollBar.setUnitIncrement(0.1 * vScrollBar.getVisibleAmount());
        }
        else {
            vScrollBar.setVisibleAmount(0.0);
            vScrollBar.setBlockIncrement(0.0);
            vScrollBar.setUnitIncrement(0.0);
        }

        if (vScrollBar.isVisible()) {
            updatePosY();
        } else {
            if (nodeHeight > contentHeight) {
                updatePosY();
            } else {
                viewContent.setLayoutY(0);
            }
        }
    }

    private double updatePosX() {
        final PFScrollPane sp = getSkinnable();
        double x = isReverseNodeOrientation() ? (hScrollBar.getMax() - (posX - hScrollBar.getMin())) : posX;
        double minX = Math.min((- x / (hScrollBar.getMax() - hScrollBar.getMin()) * (nodeWidth - contentWidth)), 0);
        viewContent.setLayoutX(snapPosition(minX));
        if (!sp.hvalueProperty().isBound()) sp.setHvalue(Utils.clamp(sp.getHmin(), posX, sp.getHmax()));
        return posX;
    }

    private double updatePosY() {
        final PFScrollPane sp = getSkinnable();
        double minY = Math.min((- posY / (vScrollBar.getMax() - vScrollBar.getMin()) * (nodeHeight - contentHeight)), 0);
        viewContent.setLayoutY(snapPosition(minY));
        if (!sp.vvalueProperty().isBound()) sp.setVvalue(Utils.clamp(sp.getVmin(), posY, sp.getVmax()));
        return posY;
    }

    private void resetClip() {
        clipRect.setWidth(snapSize(contentWidth));
        clipRect.setHeight(snapSize(contentHeight));
    }

    Timeline sbTouchTimeline;
    KeyFrame sbTouchKF1;
    KeyFrame sbTouchKF2;
    Timeline contentsToViewTimeline;
    KeyFrame contentsToViewKF1;
    KeyFrame contentsToViewKF2;
    KeyFrame contentsToViewKF3;

    private boolean tempVisibility;


    protected void startSBReleasedAnimation() {
        if (sbTouchTimeline == null) {
            sbTouchTimeline = new Timeline();
            sbTouchKF1 = new KeyFrame(Duration.millis(0), event -> {
                tempVisibility = true;
                if (touchDetected == true || mouseDown == true) {
                    sbTouchTimeline.playFromStart();
                }
            });

            sbTouchKF2 = new KeyFrame(Duration.millis(1000), event -> {
                tempVisibility = false;
                getSkinnable().requestLayout();
            });
            sbTouchTimeline.getKeyFrames().addAll(sbTouchKF1, sbTouchKF2);
        }
        sbTouchTimeline.playFromStart();
    }



    protected void startContentsToViewport() {
        double newPosX = posX;
        double newPosY = posY;

        setContentPosX(posX);
        setContentPosY(posY);

        if (posY > getSkinnable().getVmax()) {
            newPosY = getSkinnable().getVmax();
        }
        else if (posY < getSkinnable().getVmin()) {
            newPosY = getSkinnable().getVmin();
        }


        if (posX > getSkinnable().getHmax()) {
            newPosX = getSkinnable().getHmax();
        }
        else if (posX < getSkinnable().getHmin()) {
            newPosX = getSkinnable().getHmin();
        }

        if (!IS_TOUCH_SUPPORTED) {
            startSBReleasedAnimation();
        }

        if (contentsToViewTimeline != null) {
            contentsToViewTimeline.stop();
        }
        contentsToViewTimeline = new Timeline();

        contentsToViewKF1 = new KeyFrame(Duration.millis(50));

        contentsToViewKF2 = new KeyFrame(Duration.millis(150), event -> {
            getSkinnable().requestLayout();
        },
                new KeyValue(contentPosX, newPosX),
                new KeyValue(contentPosY, newPosY)
        );

        contentsToViewKF3 = new KeyFrame(Duration.millis(1500));
        contentsToViewTimeline.getKeyFrames().addAll(contentsToViewKF1, contentsToViewKF2, contentsToViewKF3);
        contentsToViewTimeline.playFromStart();
    }


    private DoubleProperty contentPosX;
    private void setContentPosX(double value) { contentPosXProperty().set(value); }
    private double getContentPosX() { return contentPosX == null ? 0.0 : contentPosX.get(); }
    private DoubleProperty contentPosXProperty() {
        if (contentPosX == null) {
            contentPosX = new DoublePropertyBase() {
                @Override protected void invalidated() {
                    hScrollBar.setValue(getContentPosX());
                    getSkinnable().requestLayout();
                }

                @Override
                public Object getBean() {
                    return PFScrollPaneSkin.this;
                }

                @Override
                public String getName() {
                    return "contentPosX";
                }
            };
        }
        return contentPosX;
    }

    private DoubleProperty contentPosY;
    private void setContentPosY(double value) { contentPosYProperty().set(value); }
    private double getContentPosY() { return contentPosY == null ? 0.0 : contentPosY.get(); }
    private DoubleProperty contentPosYProperty() {
        if (contentPosY == null) {
            contentPosY = new DoublePropertyBase() {
                @Override protected void invalidated() {
                    vScrollBar.setValue(getContentPosY());
                    getSkinnable().requestLayout();
                }

                @Override
                public Object getBean() {
                    return PFScrollPaneSkin.this;
                }

                @Override
                public String getName() {
                    return "contentPosY";
                }
            };
        }
        return contentPosY;
    }

    @Override
    protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        switch (attribute) {
            case VERTICAL_SCROLLBAR: return vScrollBar;
            case HORIZONTAL_SCROLLBAR: return hScrollBar;
            default: return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
}
