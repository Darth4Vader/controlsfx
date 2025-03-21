/**
 * Copyright (c) 2013, 2015, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.controlsfx.control;

import impl.org.controlsfx.skin.GridRow;
import impl.org.controlsfx.skin.GridRowSkin;
import impl.org.controlsfx.skin.GridViewSkin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.FocusModel;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import org.controlsfx.control.cell.ColorGridCell;

/**
 * A GridView is a virtualised control for displaying {@link #getItems()} in a
 * visual, scrollable, grid-like fashion. In other words, whereas a ListView 
 * shows one {@link ListCell} per row, in a GridView there will be zero or more
 * {@link GridCell} instances on a single row.
 * 
 * <p> This approach means that the number of GridCell instances
 * instantiated will be a significantly smaller number than the number of 
 * items in the GridView items list, as only enough GridCells are created for
 * the visible area of the GridView. This helps to improve performance and 
 * reduce memory consumption. 
 * 
 * <p>Because each {@link GridCell} extends from {@link Cell}, the same approach
 * of cell factories that is taken in other UI controls is also taken in GridView.
 * This has two main benefits: 
 * 
 * <ol>
 *   <li>GridCells are created on demand and without user involvement,
 *   <li>GridCells can be arbitrarily complex. A simple GridCell may just have 
 *   its {@link GridCell#textProperty() text property} set, whereas a more complex
 *   GridCell can have an arbitrarily complex scenegraph set inside its
 *   {@link GridCell#graphicProperty() graphic property} (as it accepts any Node).
 * </ol>
 *
 * <h3>Examples</h3>
 * <p>The following screenshot shows the GridView with the {@link ColorGridCell}
 * being used:
 * 
 * <br>
 * <img src="gridView.png" alt="Screenshot of GridView">
 * 
 * <p>To create this GridView was simple. Note that the majority of the code below
 * is related to randomly creating the colours to be represented:
 * 
 * <pre>
 * {@code
 * GridView<Color> myGrid = new GridView<>(list);
 * myGrid.setCellFactory(new Callback<GridView<Color>, GridCell<Color>>() {
 *     public GridCell<Color> call(GridView<Color> gridView) {
 *         return new ColorGridCell();
 *     }
 * });
 * Random r = new Random(System.currentTimeMillis());
 * for(int i = 0; i < 500; i++) {
 *     list.add(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 1.0));
 * }
 * }</pre>
 * 
 * @see GridCell
 */
public class GridView<T> extends ControlsFXControl {

    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/
    
    /**
     * Creates a default, empty GridView control.
     */
    public GridView() {
        this(FXCollections.<T> observableArrayList());
    }
    
    /**
     * Creates a default GridView control with the provided items prepopulated.
     * 
     * @param items The items to display inside the GridView.
     */
    public GridView(ObservableList<T> items) {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setItems(items);
    }
    
    
    
    /**************************************************************************
     * 
     * Public API
     * 
     **************************************************************************/
    
    /**
     * {@inheritDoc}
     */
    @Override protected Skin<?> createDefaultSkin() {
        return new GridViewSkin<>(this);
    }

    /** {@inheritDoc} */
    @Override public String getUserAgentStylesheet() {
        return getUserAgentStylesheet(GridView.class, "gridview.css");
    }
    
    /**************************************************************************
     * 
     * Properties
     * 
     **************************************************************************/
    
    // --- horizontal cell spacing
    /**
     * Property for specifying how much spacing there is between each cell
     * in a row (i.e. how much horizontal spacing there is).
     */
    public final DoubleProperty horizontalCellSpacingProperty() {
        if (horizontalCellSpacing == null) {
            horizontalCellSpacing = new StyleableDoubleProperty(12) {
                @Override public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return GridView.StyleableProperties.HORIZONTAL_CELL_SPACING;
                }
                
                @Override public Object getBean() {
                    return GridView.this;
                }

                @Override public String getName() {
                    return "horizontalCellSpacing"; //$NON-NLS-1$
                }
            };
        }
        return horizontalCellSpacing;
    }
    private DoubleProperty horizontalCellSpacing;
    
    /**
     * Sets the amount of horizontal spacing there should be between cells in
     * the same row.
     * @param value The amount of spacing to use.
     */
    public final void setHorizontalCellSpacing(double value) {
        horizontalCellSpacingProperty().set(value);
    }
    
    /**
     * Returns the amount of horizontal spacing there is between cells in
     * the same row.
     */
    public final double getHorizontalCellSpacing() {
        return horizontalCellSpacing == null ? 12.0 : horizontalCellSpacing.get();
    }


    
    // --- vertical cell spacing
    /**
     * Property for specifying how much spacing there is between each cell
     * in a column (i.e. how much vertical spacing there is).
     */
    private DoubleProperty verticalCellSpacing;
    public final DoubleProperty verticalCellSpacingProperty() {
        if (verticalCellSpacing == null) {
            verticalCellSpacing = new StyleableDoubleProperty(12) {
                @Override public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return GridView.StyleableProperties.VERTICAL_CELL_SPACING;
                }
                
                @Override public Object getBean() {
                    return GridView.this;
                }

                @Override public String getName() {
                    return "verticalCellSpacing"; //$NON-NLS-1$
                }
            };
        }
        return verticalCellSpacing;
    }
    
    /**
     * Sets the amount of vertical spacing there should be between cells in
     * the same column.
     * @param value The amount of spacing to use.
     */
    public final void setVerticalCellSpacing(double value) {
        verticalCellSpacingProperty().set(value);
    }

    /**
     * Returns the amount of vertical spacing there is between cells in
     * the same column.
     */
    public final double getVerticalCellSpacing() {
        return verticalCellSpacing == null ? 12.0 : verticalCellSpacing.get();
    }

    
    
    // --- cell width
    /**
     * Property representing the width that all cells should be.
     */
    public final DoubleProperty cellWidthProperty() {
        if (cellWidth == null) {
            cellWidth = new StyleableDoubleProperty(64) {
                @Override public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return GridView.StyleableProperties.CELL_WIDTH;
                }
                
                @Override public Object getBean() {
                    return GridView.this;
                }

                @Override public String getName() {
                    return "cellWidth"; //$NON-NLS-1$
                }
            };
        }
        return cellWidth;
    }
    private DoubleProperty cellWidth;

    /**
     * Sets the width that all cells should be.
     */
    public final void setCellWidth(double value) {
        cellWidthProperty().set(value);
    }

    /**
     * Returns the width that all cells should be.
     */
    public final double getCellWidth() {
        return cellWidth == null ? 64.0 : cellWidth.get();
    }

    
    // --- cell height
    /**
     * Property representing the height that all cells should be.
     */
    public final DoubleProperty cellHeightProperty() {
        if (cellHeight == null) {
            cellHeight = new StyleableDoubleProperty(64) {
                @Override public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return GridView.StyleableProperties.CELL_HEIGHT;
                }
                
                @Override public Object getBean() {
                    return GridView.this;
                }

                @Override public String getName() {
                    return "cellHeight"; //$NON-NLS-1$
                }
            };
        }
        return cellHeight;
    }
    private DoubleProperty cellHeight;

    /**
     * Sets the height that all cells should be.
     */
    public final void setCellHeight(double value) {
        cellHeightProperty().set(value);
    }

    /**
     * Returns the height that all cells should be.
     */
    public final double getCellHeight() {
        return cellHeight == null ? 64.0 : cellHeight.get();
    }

    
    // I've removed this functionality until there is a clear need for it.
    // To re-enable it, there is code in GridRowSkin that has been commented
    // out that must be re-enabled.
    // Don't forget also to enable the styleable property further down in this
    // class.
//    // --- horizontal alignment
//    private ObjectProperty<HPos> horizontalAlignment;
//    public final ObjectProperty<HPos> horizontalAlignmentProperty() {
//        if (horizontalAlignment == null) {
//            horizontalAlignment = new StyleableObjectProperty<HPos>(HPos.CENTER) {
//                @Override public CssMetaData<GridView<?>,HPos> getCssMetaData() {
//                    return GridView.StyleableProperties.HORIZONTAL_ALIGNMENT;
//                }
//                
//                @Override public Object getBean() {
//                    return GridView.this;
//                }
//
//                @Override public String getName() {
//                    return "horizontalAlignment";
//                }
//            };
//        }
//        return horizontalAlignment;
//    }
//
//    public final void setHorizontalAlignment(HPos value) {
//        horizontalAlignmentProperty().set(value);
//    }
//
//    public final HPos getHorizontalAlignment() {
//        return horizontalAlignment == null ? HPos.CENTER : horizontalAlignment.get();
//    }

    
    // --- cell factory
    /**
     * Property representing the cell factory that is currently set in this
     * GridView, or null if no cell factory has been set (in which case the 
     * default cell factory provided by the GridView skin will be used). The cell
     * factory is used for instantiating enough GridCell instances for the 
     * visible area of the GridView. Refer to the GridView class documentation
     * for more information and examples.
     */
    public final ObjectProperty<Callback<GridView<T>, GridCell<T>>> cellFactoryProperty() {
        if (cellFactory == null) {
            cellFactory = new SimpleObjectProperty<>(this, "cellFactory"); //$NON-NLS-1$
        }
        return cellFactory;
    }
    private ObjectProperty<Callback<GridView<T>, GridCell<T>>> cellFactory;

    /**
     * Sets the cell factory to use to create {@link GridCell} instances to 
     * show in the GridView.
     */
    public final void setCellFactory(Callback<GridView<T>, GridCell<T>> value) {
        cellFactoryProperty().set(value);
    }

    /**
     * Returns the cell factory that will be used to create {@link GridCell} 
     * instances to show in the GridView.
     */
    public final Callback<GridView<T>, GridCell<T>> getCellFactory() {
        return cellFactory == null ? null : cellFactory.get();
    }

    
    // --- items
    /**
     * The items to be displayed in the GridView (as rendered via {@link GridCell}
     * instances). For example, if the {@link ColorGridCell} were being used
     * (as in the case at the top of this class documentation), this items list
     * would be populated with {@link Color} values. It is important to 
     * appreciate that the items list is used for the data, not the rendering.
     * What is meant by this is that the items list should contain Color values,
     * not the {@link Node nodes} that represent the Color. The actual rendering
     * should be left up to the {@link #cellFactoryProperty() cell factory},
     * where it will take the Color value and create / update the display as
     * necessary. 
     */
    public final ObjectProperty<ObservableList<T>> itemsProperty() {
        if (items == null) {
            items = new SimpleObjectProperty<>(this, "items"); //$NON-NLS-1$
        }
        return items;
    }
    private ObjectProperty<ObservableList<T>> items;
    
    /**
     * Sets a new {@link ObservableList} as the items list underlying GridView.
     * The old items list will be discarded.
     */
    public final void setItems(ObservableList<T> value) {
        itemsProperty().set(value);
    }

    /**
     * Returns the currently-in-use items list that is being used by the
     * GridView.
     */
    public final ObservableList<T> getItems() {
        return items == null ? null : items.get();
    }

    
    
    
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "grid-view"; //$NON-NLS-1$

    /** @treatAsPrivate */
    private static class StyleableProperties {
        private static final CssMetaData<GridView<?>,Number> HORIZONTAL_CELL_SPACING = 
            new CssMetaData<GridView<?>,Number>("-fx-horizontal-cell-spacing", StyleConverter.getSizeConverter(), 12d) { //$NON-NLS-1$

            @Override public Double getInitialValue(GridView<?> node) {
                return node.getHorizontalCellSpacing();
            }

            @Override public boolean isSettable(GridView<?> n) {
                return n.horizontalCellSpacing == null || !n.horizontalCellSpacing.isBound();
            }

            @Override
            @SuppressWarnings("unchecked")
            public StyleableProperty<Number> getStyleableProperty(GridView<?> n) {
                return (StyleableProperty<Number>)n.horizontalCellSpacingProperty();
            }
        };
        
        private static final CssMetaData<GridView<?>,Number> VERTICAL_CELL_SPACING = 
            new CssMetaData<GridView<?>,Number>("-fx-vertical-cell-spacing", StyleConverter.getSizeConverter(), 12d) { //$NON-NLS-1$

            @Override public Double getInitialValue(GridView<?> node) {
                return node.getVerticalCellSpacing();
            }

            @Override public boolean isSettable(GridView<?> n) {
                return n.verticalCellSpacing == null || !n.verticalCellSpacing.isBound();
            }

            @Override
            @SuppressWarnings("unchecked")
            public StyleableProperty<Number> getStyleableProperty(GridView<?> n) {
                return (StyleableProperty<Number>)n.verticalCellSpacingProperty();
            }
        };
        
        private static final CssMetaData<GridView<?>,Number> CELL_WIDTH = 
            new CssMetaData<GridView<?>,Number>("-fx-cell-width", StyleConverter.getSizeConverter(), 64d) { //$NON-NLS-1$

            @Override public Double getInitialValue(GridView<?> node) {
                return node.getCellWidth();
            }

            @Override public boolean isSettable(GridView<?> n) {
                return n.cellWidth == null || !n.cellWidth.isBound();
            }

            @Override
            @SuppressWarnings("unchecked")
            public StyleableProperty<Number> getStyleableProperty(GridView<?> n) {
                return (StyleableProperty<Number>)n.cellWidthProperty();
            }
        };
        
        private static final CssMetaData<GridView<?>,Number> CELL_HEIGHT = 
            new CssMetaData<GridView<?>,Number>("-fx-cell-height", StyleConverter.getSizeConverter(), 64d) { //$NON-NLS-1$

            @Override public Double getInitialValue(GridView<?> node) {
                return node.getCellHeight();
            }

            @Override public boolean isSettable(GridView<?> n) {
                return n.cellHeight == null || !n.cellHeight.isBound();
            }

            @Override
            @SuppressWarnings("unchecked")
            public StyleableProperty<Number> getStyleableProperty(GridView<?> n) {
                return (StyleableProperty<Number>)n.cellHeightProperty();
            }
        };
        
//        private static final CssMetaData<GridView<?>,HPos> HORIZONTAL_ALIGNMENT = 
//            new CssMetaData<GridView<?>,HPos>("-fx-horizontal_alignment",
//                new EnumConverter<HPos>(HPos.class), 
//                HPos.CENTER) {
//
//            @Override public HPos getInitialValue(GridView node) {
//                return node.getHorizontalAlignment();
//            }
//
//            @Override public boolean isSettable(GridView n) {
//                return n.horizontalAlignment == null || !n.horizontalAlignment.isBound();
//            }
//
//            @Override public StyleableProperty<HPos> getStyleableProperty(GridView n) {
//                return (StyleableProperty<HPos>)n.horizontalAlignmentProperty();
//            }
//        };
            
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Control.getClassCssMetaData());
            styleables.add(HORIZONTAL_CELL_SPACING);
            styleables.add(VERTICAL_CELL_SPACING);
            styleables.add(CELL_WIDTH);
            styleables.add(CELL_HEIGHT);
//            styleables.add(HORIZONTAL_ALIGNMENT);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
    
    // --- Focus Model
    private ObjectProperty<GridViewFocusModel<T>> focusModel;
    public final void setFocusModel(GridViewFocusModel<T> value) {
        focusModelProperty().set(value);
    }
    public final GridViewFocusModel<T> getFocusModel() {
        return focusModel == null ? null : focusModel.get();
    }
    /**
     * Represents the currently-installed {@link TableViewFocusModel} for this
     * TableView. Under almost all circumstances leaving this as the default
     * focus model will suffice.
     * @return focusModel property
     */
    public final ObjectProperty<GridViewFocusModel<T>> focusModelProperty() {
        if (focusModel == null) {
            focusModel = new SimpleObjectProperty<>(this, "focusModel");
        }
        return focusModel;
    }
    
    /**
     * A {@link FocusModel} with additional functionality to support the requirements
     * of a GridView control.
     *
     * @param <S> the type of the item contained within the GridView
     * @see GridView
     * @since JavaFX 2.0
     */
    public static class GridViewFocusModel<T> extends FocusModel<T> {

        private final GridView<T> gridView;

        private final GridViewPosition<T> EMPTY_CELL;

        /**
         * Creates a default GridViewFocusModel instance that will be used to
         * manage focus of the provided GridView control.
         *
         * @param gridView The gridView upon which this focus model operates.
         * @throws NullPointerException The GridView argument can not be null.
         */
        public GridViewFocusModel(final GridView<T> gridView) {
            if (gridView == null) {
                throw new NullPointerException("GridView can not be null");
            }

            this.gridView = gridView;
            this.EMPTY_CELL = new GridViewPosition<>(gridView, null, -1);

            itemsObserver = new InvalidationListener() {
                private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(gridView.getItems());

                @Override public void invalidated(Observable observable) {
                    ObservableList<T> oldItems = weakItemsRef.get();
                    weakItemsRef = new WeakReference<>(gridView.getItems());
                    updateItemsObserver(oldItems, gridView.getItems());
                }
            };
            this.gridView.itemsProperty().addListener(new WeakInvalidationListener(itemsObserver));
            if (gridView.getItems() != null) {
                this.gridView.getItems().addListener(weakItemsContentListener);
            }

            updateDefaultFocus();

            focusedCellProperty().addListener(o -> {
                //gridView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
            	System.out.println("changed22");
            	System.out.println(getFocusedIndex());
            	GridRow<T> row = (GridRow<T>) gridView.queryAccessibleAttribute(AccessibleAttribute.FOCUS_ITEM);
            	System.out.println(row);
            	GridRowSkin<?> skin = (GridRowSkin<?>) row.getSkin();
            	System.out.println("Hell " + skin.getCellAtIndex(0).getClass());
            	skin.getCellAtIndex(0).requestFocus();
            	System.out.println(gridView.getScene().getFocusOwner());
            });
        }

        private final InvalidationListener itemsObserver;

        // Listen to changes in the tableview items list, such that when it
        // changes we can update the focused index to refer to the new indices.
        private final ListChangeListener<T> itemsContentListener = c -> {
            c.next();

            if (c.wasReplaced() || c.getAddedSize() == getItemCount()) {
                updateDefaultFocus();
                return;
            }

            GridViewPosition<T> focusedCell = getFocusedCell();
            GridView<T> gridView = focusedCell.getGridView();
            if(gridView == null)
            	return;
            final int focusedIndex = gridView.getIndexOfPosition(focusedCell);
            if (focusedIndex == -1 || c.getFrom() > focusedIndex) {
                return;
            }

            c.reset();
            boolean added = false;
            boolean removed = false;
            int addedSize = 0;
            int removedSize = 0;
            while (c.next()) {
                added |= c.wasAdded();
                removed |= c.wasRemoved();
                addedSize += c.getAddedSize();
                removedSize += c.getRemovedSize();
            }
            System.out.println("Something");
            if (added && ! removed) {
                if (addedSize < c.getList().size()) {
                    final int newFocusIndex = Math.min(getItemCount() - 1, getFocusedIndex() + addedSize);
                    focus(newFocusIndex);
                }
            } else if (!added && removed) {
                final int newFocusIndex = Math.max(0, getFocusedIndex() - removedSize);
                if (newFocusIndex < 0) {
                    focus(focusedCell.getGridRow(), 0);
                } else {
                    focus(newFocusIndex);
                }
            }
        };

        private WeakListChangeListener<T> weakItemsContentListener
                = new WeakListChangeListener<>(itemsContentListener);

        private void updateItemsObserver(ObservableList<T> oldList, ObservableList<T> newList) {
            // the tableview items list has changed, we need to observe
            // the new list, and remove any observer we had from the old list
            if (oldList != null) oldList.removeListener(weakItemsContentListener);
            if (newList != null) newList.addListener(weakItemsContentListener);

            updateDefaultFocus();
        }

        /** {@inheritDoc} */
        protected int getItemCount() {
            if (gridView.getItems() == null) return -1;
            return gridView.getItems().size();
        }

        /** {@inheritDoc} */
        protected T getModelItem(int index) {
            if (gridView.getItems() == null) return null;

            if (index < 0 || index >= getItemCount()) return null;

            return gridView.getItems().get(index);
        }

        /**
         * The position of the current item in the GridView which has the focus.
         */
        private ReadOnlyObjectWrapper<GridViewPosition<T>> focusedCell;
        public final ReadOnlyObjectProperty<GridViewPosition<T>> focusedCellProperty() {
            return focusedCellPropertyImpl().getReadOnlyProperty();
        }
        private void setFocusedCell(GridViewPosition<T> value) {
        	System.out.println(getFocusedCell());
        	System.out.println("Le't Set a new Value");
        	System.out.println(value);
        	focusedCellPropertyImpl().set(value);
        }
        public final GridViewPosition<T> getFocusedCell() { return focusedCell == null ? EMPTY_CELL : focusedCell.get(); }

        private ReadOnlyObjectWrapper<GridViewPosition<T>> focusedCellPropertyImpl() {
            if (focusedCell == null) {
                focusedCell = new ReadOnlyObjectWrapper<>(EMPTY_CELL) {
                    private GridViewPosition<T> old;
                    @Override protected void invalidated() {
                        if (get() == null) return;

                        if (old == null || !old.equals(get())) {
                            // manually update the focus properties to ensure consistency
                        	GridViewFocusModel.super.focus(gridView.getIndexOfPosition(get()));

                            old = get();
                        }
                    }

                    @Override
                    public Object getBean() {
                        return GridViewFocusModel.this;
                    }

                    @Override
                    public String getName() {
                        return "focusedCell";
                    }
                };
            }
            return focusedCell;
        }


        /**
         * Causes the item at the given index to receive the focus.
         *
         * @param row The row of the item to give focus to. Can be null.
         * @param column The column index of the item to give focus to.
         */
        public void focus(GridRow<T> row, int column) {
            if (column < 0 || column >= getItemCount()) {
                setFocusedCell(EMPTY_CELL);
            } else {
                GridViewPosition<T> oldFocusCell = getFocusedCell();
                GridViewPosition<T> newFocusCell = new GridViewPosition<>(gridView, row, column);
                setFocusedCell(newFocusCell);

                if (newFocusCell.equals(oldFocusCell)) {
                    // manually update the focus properties to ensure consistency
                	super.focus(gridView.getIndexOfPosition(newFocusCell));
                }
            }
        }

        /**
         * Convenience method for setting focus on a particular row or cell
         * using a {@link GridViewPosition}.
         *
         * @param pos The table position where focus should be set.
         */
        public void focus(GridViewPosition<T> pos) {
            if (pos == null) return;
            focus(pos.getGridRow(), pos.getColumn());
        }


        /* *********************************************************************
         *                                                                     *
         * Public API                                                          *
         *                                                                     *
         **********************************************************************/

        /**
         * Tests whether the row / cell at the given location currently has the
         * focus within the GridView.
         */
        public boolean isFocused(GridRow<T> row, int column) {
            if (column < 0) return false;

            GridViewPosition<T> cell = getFocusedCell();
            boolean rowMatch = row == null || row.equals(cell.getGridRow());

            return rowMatch && cell.getColumn() == column;
        }

        /**
         * Causes the item at the given index to receive the focus. This does not
         * cause the current selection to change. Updates the focusedItem and
         * focusedIndex properties such that <code>focusedIndex = -1</code> unless
         * <pre><code>0 &lt;= index &lt; model size</code></pre>.
         *
         * @param index The index of the item to get focus.
         */
        @Override public void focus(int index) {
        	System.out.println("Focus Me: " + index + " , " + getItemCount());
            if (index < 0 || index >= getItemCount()) {
                setFocusedCell(EMPTY_CELL);
            } else {
            	//GridRow<T> row = gridView.in
            	setFocusedCell(gridView.getPositionFromIndex(index));
                //setFocusedCell(new GridViewPosition<>(gridView, null, index));
            }
        }

        /**
         * Attempts to move focus to the cell above the currently focused cell.
         */
        public void focusAboveCell() {
            /*GridViewPosition<T> cell = getFocusedCell();

            if (getFocusedIndex() == -1) {
                focus(getItemCount() - 1, cell.getTableColumn());
            } else if (getFocusedIndex() > 0) {
            	GridRow<T> row = cell.getGridRow();
            	
                focus(getGridRow(row, 1), cell.getColumn());
            }*/
            GridViewPosition<T> cell = getFocusedCell();
            if (cell.getRow() <= 0) return;
            focus(getGridRow(cell.getGridRow(), -1), cell.getColumn());
        }

        /**
         * Attempts to move focus to the cell below the currently focused cell.
         */
        public void focusBelowCell() {
            /*
        	GridViewPosition<T> cell = getFocusedCell();
            if (getFocusedIndex() == -1) {
                focus(0, cell.getTableColumn());
            } else if (getFocusedIndex() != getItemCount() -1) {
                focus(getFocusedIndex() + 1, cell.getTableColumn());
            }
            */
            GridViewPosition<T> cell = getFocusedCell();
            System.out.println(cell.getGridRow());
            System.out.println(cell.getRow() + " Row Cpunt: " + getRowCount());
            if (cell.getRow() == getRowCount() - 1) return;
            focus(getGridRow(cell.getGridRow(), 1), cell.getColumn());
        }

        /**
         * Attempts to move focus to the cell to the left of the currently focused cell.
         */
        public void focusLeftCell() {
            /*
        	GridViewPosition<T> cell = getFocusedCell();
            if (cell.getColumn() <= 0) return;
            focus(cell.getRow(), getTableColumn(cell.getTableColumn(), -1));
            */
        }

        /**
         * Attempts to move focus to the cell to the right of the the currently focused cell.
         */
        public void focusRightCell() {
        	/*
            GridViewPosition<T> cell = getFocusedCell();
            if (cell.getColumn() == getColumnCount() - 1) return;
            focus(cell.getRow(), getTableColumn(cell.getTableColumn(), 1));
            */
        }

        /** {@inheritDoc} */
        public void focusPrevious() {
        	/*
            if (getFocusedIndex() == -1) {
                focus(0);
            } else if (getFocusedIndex() > 0) {
                focusAboveCell();
            }
            */
        }

        /** {@inheritDoc} */
        public void focusNext() {
        	/*
            if (getFocusedIndex() == -1) {
                focus(0);
            } else if (getFocusedIndex() != getItemCount() -1) {
                focusBelowCell();
            }
            */
        }

        /* *********************************************************************
         *                                                                     *
         * Private Implementation                                              *
         *                                                                     *
         **********************************************************************/

        private void updateDefaultFocus() {
            // when the items list totally changes, we should clear out
            // the focus
            int newValueIndex = -1;
            if (gridView.getItems() != null) {
                T focusedItem = getFocusedItem();
                if (focusedItem != null) {
                    newValueIndex = gridView.getItems().indexOf(focusedItem);
                }

                // we put focus onto the first item, if there is at least
                // one item in the list
                if (newValueIndex == -1) {
                    newValueIndex = gridView.getItems().size() > 0 ? 0 : -1;
                }
            }

            focus(newValueIndex);
        }

        private int getRowCount() {
            return gridView.getNumberOfRows();
        }

        // Gets a table column to the left or right of the current one, given an offset
        private GridRow<T> getGridRow(GridRow<T> row, int offset) {
            int rowIndex = gridView.getVisibleRowIndex(row);
            int newRowIndex = rowIndex + offset;
            System.out.println("Row: " + row.getIndex() + ", RowIndex: " + rowIndex + ", NewRowIndex: " + newRowIndex);
            return gridView.getVisibleRow(newRowIndex);
        }
    }
    
    public int getVisibleRowIndex(GridRow<T> row) {
    	System.out.println("getVisibleRowIndex: " + row);
    	Skin<?> skin = getSkin();
    	if(skin instanceof GridViewSkin) {
    		GridViewSkin<?> gskin = (GridViewSkin<?>) skin;
    		return gskin.getVisibleRowIndex(row.getIndex());
    	}
    	return -1;
    }
    
    public final GridRow<T> getVisibleRow(int index) {
    	System.out.println("getVisibleRow: " + index);
    	Skin<?> skin = getSkin();
    	if(skin instanceof GridViewSkin) {
    		GridViewSkin<T> gskin = (GridViewSkin<T>) skin;
    		return gskin.getVisibleRow(index);
    	}
    	return null;
    }
    
    public int getNumberOfRows() {
    	System.out.println("getNumberOfRows");
    	Skin<?> skin = getSkin();
    	if(skin instanceof GridViewSkin) {
    		GridViewSkin<T> gskin = (GridViewSkin<T>) skin;
    		return gskin.getNumberOfRows();
    	}
    	return -1;
    }
    
    public int getIndexOfPosition(GridViewPosition<T> pos) {
    	System.out.println("getIndexOfPosition: " + pos);
    	Skin<?> skin = getSkin();
    	if(skin instanceof GridViewSkin) {
    		GridViewSkin<?> gskin = (GridViewSkin<?>) skin;
			int rowIndex = pos.getRow();
			if (rowIndex >= 0) {
				int numOfRows = gskin.getNumberOfRows();
				if(rowIndex < numOfRows) {
					int index = 0;
					for (int i = 0; i < rowIndex; i++) {
						index += gskin.getNumberOfColumnsInRow(i);
					}
					return index + pos.getColumn();
				}
			}
    	}
    	return -1;
    }
    
    public GridViewPosition<T> getPositionFromIndex(int index) {
    	System.out.println("getPositionFromIndex: " + index);
    	Skin<?> skin = getSkin();
    	if(skin instanceof GridViewSkin) {
    		GridViewSkin<?> gskin = (GridViewSkin<?>) skin;
    		int numOfRows = gskin.getNumberOfRows();
    		int idx = 0;
			for (int i = 0; i < numOfRows; i++) {
				int columns = gskin.getNumberOfColumnsInRow(i);
				System.out.println(idx + "<=" + index + "<=" + (idx + columns));
				if(idx <= index && index <= (idx + columns))
					return new GridViewPosition<>(this, this.getVisibleRow(i), index - idx);
				idx += columns;
			}
    	}
    	return null;
    }
    
    public int getNumberOfColumnsInRow(int row) {
    	System.out.println("getNumberOfColumnsInRow: " + row);
    	Skin<?> skin = getSkin();
    	if(skin instanceof GridViewSkin) {
    		GridViewSkin<?> gskin = (GridViewSkin<?>) skin;
    		return gskin.getNumberOfColumnsInRow(row);
    	}
    	return -1;
    }
    
    /** {@inheritDoc} */
    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
    	switch (attribute) {
            case FOCUS_ITEM: {
                Node row = (Node)super.queryAccessibleAttribute(attribute, parameters);
                if (row == null) return null;
                Node cell = (Node)row.queryAccessibleAttribute(attribute, parameters);
                System.out.println("Row: " + row);
                System.out.println("Cell: " + cell);
                /* cell equals to null means the row is a placeholder node */
                return cell != null ?  cell : row;
            }
            case FOCUSED: {
            	System.out.println("Far");
            	return super.queryAccessibleAttribute(attribute, parameters);
            }
            default: return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
}
