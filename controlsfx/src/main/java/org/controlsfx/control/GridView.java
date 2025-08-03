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

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.controlsfx.control.cell.ColorGridCell;

import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import impl.org.controlsfx.skin.GridViewSkin;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
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
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Pair;

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
        
        // ...focus model
        setFocusModel(new GridView.GridViewFocusModel<>(this));
        
        setSelectionModel(new GridViewMultipleSelectionModel<>(this));
        
        
		skinProperty().addListener((ov, oldSkin, newSkin) -> {
			if (newSkin != null && getFocusModel() != null) {
				getFocusModel().focus(0);
			}
		});
    }
    
    
    
    /**************************************************************************
     * 
     * Public API
     * 
     **************************************************************************/
    
    /**
     * Scrolls the GridView such that the item in the given index is visible to
     * the end user.
     *
     * @param index The index that should be made visible to the user, assuming
     *      of course that it is greater than, or equal to 0, and less than the
     *      size of the items list contained within the given GridView.
     */
    public void scrollTo(int index) {
    	Skin<?> skin = getSkin();
		if (skin instanceof GridViewSkin) {
			((GridViewSkin<?>) skin).scrollTo(index);
		}
    }

    /**
     * Scrolls the GridView so that the given object is visible within the viewport.
     * @param object The object that should be visible to the user.
     * @since JavaFX 8.0
     */
    public void scrollTo(T object) {
        if( getItems() != null ) {
            int idx = getItems().indexOf(object);
            if( idx >= 0 ) {
            	scrollTo(idx);
            }
        }
    }
    
	public int getRowFromIndex(int index) {
		if(index >= 0) {
			Skin<?> skin = getSkin();
			if (skin instanceof GridViewSkin) {
				GridViewSkin<?> gskin = (GridViewSkin<?>) skin;
				int maxCellsInRow = gskin.computeMaxCellsInRow();
				return (int) Math.floor(index / maxCellsInRow);
			}
		}
		return -1;
    }
	
	public int getColumnFromIndex(int index) {
		if(index >= 0) {
			Skin<?> skin = getSkin();
			if (skin instanceof GridViewSkin) {
				GridViewSkin<?> gskin = (GridViewSkin<?>) skin;
				int maxCellsInRow = gskin.computeMaxCellsInRow();
				return index % maxCellsInRow;
			}
		}
		return -1;
	}
    
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
    
    // --- Selection Model
    private ObjectProperty<GridViewMultipleSelectionModel<T>> selectionModel = new SimpleObjectProperty<>(this, "selectionModel");

    /**
     * Sets the {@link MultipleSelectionModel} to be used in the GridView.
     * Despite a GridView requiring a <b>Multiple</b>SelectionModel, it is possible
     * to configure it to only allow single selection (see
     * {@link MultipleSelectionModel#setSelectionMode(javafx.scene.control.SelectionMode)}
     * for more information).
     * @param value the MultipleSelectionModel to be used in this GridView
     */
    public final void setSelectionModel(GridViewMultipleSelectionModel<T> value) {
        selectionModelProperty().set(value);
    }

    /**
     * Returns the currently installed selection model.
     * @return the currently installed selection model
     */
    public final GridViewMultipleSelectionModel<T> getSelectionModel() {
        return selectionModel == null ? null : selectionModel.get();
    }

    /**
     * The SelectionModel provides the API through which it is possible
     * to select single or multiple items within a GirdView, as  well as inspect
     * which items have been selected by the user. Note that it has a generic
     * type that must match the type of the GridView itself.
     * @return the selectionModel property
     */
    public final ObjectProperty<GridViewMultipleSelectionModel<T>> selectionModelProperty() {
        return selectionModel;
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
     * Represents the currently-installed {@link GridViewFocusModel} for this
     * GridView. Under almost all circumstances leaving this as the default
     * focus model will suffice.
     * @return focusModel property
     */
    public final ObjectProperty<GridViewFocusModel<T>> focusModelProperty() {
        if (focusModel == null) {
            focusModel = new SimpleObjectProperty<>(this, "focusModel");
        }
        return focusModel;
    }
    
    // package for testing
    public static class GridViewMultipleSelectionModel<T> extends TableSelectionModel<T> {

        /* *********************************************************************
         *                                                                     *
         * Constructors                                                        *
         *                                                                     *
         **********************************************************************/
    	
    	private Method startAtomic;
    	private Method stopAtomic;
    	private Method shiftSelection;

        public GridViewMultipleSelectionModel(final GridView<T> gridView) {
            if (gridView == null) {
                throw new IllegalArgumentException("GridView can not be null");
            }

            this.gridView = gridView;
            
            try {
	            startAtomic = TableSelectionModel.class.getSuperclass().getDeclaredMethod("startAtomic");
	            stopAtomic = TableSelectionModel.class.getSuperclass().getDeclaredMethod("stopAtomic");
	            shiftSelection = TableSelectionModel.class.getSuperclass().getDeclaredMethod("shiftSelection", List.class, Callback.class);
	            shiftSelection.trySetAccessible();
            }
			catch (Exception e) {
				throw new IllegalArgumentException("Cannot Access Atmoic Methods", e);
			}

            /*
             * The following two listeners are used in conjunction with
             * SelectionModel.select(T obj) to allow for a developer to select
             * an item that is not actually in the data model. When this occurs,
             * we actively try to find an index that matches this object, going
             * so far as to actually watch for all changes to the items list,
             * rechecking each time.
             */
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
                this.gridView.getItems().addListener(weakItemsContentObserver);
            }

            updateItemCount();

            updateDefaultSelection();
        }

        // watching for changes to the items list content
        private final ListChangeListener<T> itemsContentObserver = new ListChangeListener<>() {
            @Override public void onChanged(Change<? extends T> c) {
            	try {
	                updateItemCount();
	
	                boolean doSelectionUpdate = true;
	
	                while (c.next()) {
	                    final T selectedItem = getSelectedItem();
	                    final int selectedIndex = getSelectedIndex();
	
	                    if (gridView.getItems() == null || gridView.getItems().isEmpty()) {
	                        clearSelection();
	                    } else if (selectedIndex == -1 && selectedItem != null) {
	                        int newIndex = gridView.getItems().indexOf(selectedItem);
	                        if (newIndex != -1) {
	                            setSelectedIndex(newIndex);
	                            doSelectionUpdate = false;
	                        }
	                    } else if (c.wasRemoved() &&
	                            c.getRemovedSize() == 1 &&
	                            ! c.wasAdded() &&
	                            selectedItem != null &&
	                            selectedItem.equals(c.getRemoved().get(0))) {
	                        // Bug fix for RT-28637
	                        if (getSelectedIndex() < getItemCount()) {
	                            final int previousRow = selectedIndex == 0 ? 0 : selectedIndex - 1;
	                            T newSelectedItem = getModelItem(previousRow);
	                            if (! selectedItem.equals(newSelectedItem)) {
	                            	startAtomic.invoke(this);
	                            	//startAtomic();
	                                clearSelection(selectedIndex);
	                                stopAtomic.invoke(this);
	                                //stopAtomic();
	                                select(newSelectedItem);
	                            }
	                        }
	                    }
	                }
	
	                if (doSelectionUpdate) {
	                    updateSelection(c);
	                }
            	}
				catch (Exception e) {
					throw new IllegalArgumentException("Problem with itemsContentObserver", e);
				}
            }
        };

        // watching for changes to the items list
        private final InvalidationListener itemsObserver;

        private WeakListChangeListener<T> weakItemsContentObserver =
                new WeakListChangeListener<>(itemsContentObserver);




        /* *********************************************************************
         *                                                                     *
         * Internal properties                                                 *
         *                                                                     *
         **********************************************************************/

        private final GridView<T> gridView;

        private int itemCount = 0;

        private int previousModelSize = 0;

        // Listen to changes in the listview items list, such that when it
        // changes we can update the selected indices bitset to refer to the
        // new indices.
        // At present this is basically a left/right shift operation, which
        // seems to work ok.
        private void updateSelection(Change<? extends T> c) {
//            // debugging output
//            System.out.println(gridView.getId());
//            if (c.wasAdded()) {
//                System.out.println("\tAdded size: " + c.getAddedSize() + ", Added sublist: " + c.getAddedSubList());
//            }
//            if (c.wasRemoved()) {
//                System.out.println("\tRemoved size: " + c.getRemovedSize() + ", Removed sublist: " + c.getRemoved());
//            }
//            if (c.wasReplaced()) {
//                System.out.println("\tWas replaced");
//            }
//            if (c.wasPermutated()) {
//                System.out.println("\tWas permutated");
//            }
            c.reset();

            List<Pair<Integer, Integer>> shifts = new ArrayList<>();
            while (c.next()) {
                if (c.wasReplaced()) {
                    if (c.getList().isEmpty()) {
                        // the entire items list was emptied - clear selection
                        clearSelection();
                    } else {
                        int index = getSelectedIndex();

                        if (previousModelSize == c.getRemovedSize()) {
                            // all items were removed from the model
                            clearSelection();
                        } else if (index < getItemCount() && index >= 0) {
                            // Fix for RT-18969: the list had setAll called on it
                            // Use of makeAtomic is a fix for RT-20945
                        	try {
	                        	startAtomic.invoke(this);
	                        	//startAtomic();
	                            clearSelection(index);
	                            stopAtomic.invoke(this);
	                            //stopAtomic();
	                            select(index);
                        	}
							catch (Exception e) {
								throw new IllegalArgumentException("Problem with updateSelection", e);
							}
                        } else {
                            // Fix for RT-22079
                            clearSelection();
                        }
                    }
                } else if (c.wasAdded() || c.wasRemoved()) {
                    int shift = c.wasAdded() ? c.getAddedSize() : -c.getRemovedSize();
                    shifts.add(new Pair<>(c.getFrom(), shift));
                } else if (c.wasPermutated()) {

                    // General approach:
                    //   -- detected a sort has happened
                    //   -- Create a permutation lookup map (1)
                    //   -- dump all the selected indices into a list (2)
                    //   -- clear the selected items / indexes (3)
                    //   -- create a list containing the new indices (4)
                    //   -- for each previously-selected index (5)
                    //     -- if index is in the permutation lookup map
                    //       -- add the new index to the new indices list
                    //   -- Perform batch selection (6)

                    // (1)
                    int length = c.getTo() - c.getFrom();
                    HashMap<Integer, Integer> pMap = new HashMap<>(length);
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        pMap.put(i, c.getPermutation(i));
                    }

                    // (2)
                    List<Integer> selectedIndices = new ArrayList<>(getSelectedIndices());


                    // (3)
                    clearSelection();

                    // (4)
                    List<Integer> newIndices = new ArrayList<>(getSelectedIndices().size());

                    // (5)
                    for (int i = 0; i < selectedIndices.size(); i++) {
                        int oldIndex = selectedIndices.get(i);

                        if (pMap.containsKey(oldIndex)) {
                            Integer newIndex = pMap.get(oldIndex);
                            newIndices.add(newIndex);
                        }
                    }

                    // (6)
                    if (!newIndices.isEmpty()) {
                        if (newIndices.size() == 1) {
                            select(newIndices.get(0));
                        } else {
                            int[] ints = new int[newIndices.size() - 1];
                            for (int i = 0; i < newIndices.size() - 1; i++) {
                                ints[i] = newIndices.get(i + 1);
                            }
                            selectIndices(newIndices.get(0), ints);
                        }
                    }
                }
            }

            if (!shifts.isEmpty()) {
            	// Not Working Problem
            	/*try {
            		shiftSelection.invoke((TableSelectionModel.class.getSuperclass()).cast(this), shifts, null);
            	}
                catch (Exception e) {
                	e.printStackTrace();
                }*/
                //shiftSelection(shifts, null);
            }

            previousModelSize = getItemCount();
        }



        /* *********************************************************************
         *                                                                     *
         * Public selection API                                                *
         *                                                                     *
         **********************************************************************/

        /** {@inheritDoc} */
        @Override public void selectAll() {
            // when a selectAll happens, the anchor should not change, so we store it
            // before, and restore it afterwards
            final int anchor = ListCellBehavior.getAnchor(gridView, -1);
            super.selectAll();
            ListCellBehavior.setAnchor(gridView, anchor, false);
        }

        /** {@inheritDoc} */
        @Override public void clearAndSelect(int row) {
            ListCellBehavior.setAnchor(gridView, row, false);
            super.clearAndSelect(row);
        }

        /** {@inheritDoc} */
        @Override protected void focus(int row) {
            if (gridView.getFocusModel() == null) return;
            gridView.getFocusModel().focus(row);

            gridView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
        }

        /** {@inheritDoc} */
        @Override protected int getFocusedIndex() {
            if (gridView.getFocusModel() == null) return -1;
            return gridView.getFocusModel().getFocusedIndex();
        }

        @Override protected int getItemCount() {
            return itemCount;
        }

        @Override protected T getModelItem(int index) {
            List<T> items = gridView.getItems();
            if (items == null) return null;
            if (index < 0 || index >= itemCount) return null;

            return items.get(index);
        }
        
        public void select(int row, int column) {
        	int index = gridView.getIndexInRowColumn(row, column);
        	if(index != -1) {
        		select(index);
        	}
        }
        
        /**
         * Attempts to move focus to the cell above the currently focused cell.
         */
        @Override public void selectAboveCell() {
			int focusedIndex = getFocusedIndex();
			if (focusedIndex != -1) {
				int row = gridView.getRowFromIndex(focusedIndex);
				int column = gridView.getColumnFromIndex(focusedIndex);
				if (row != -1 && column != -1) {
					select(row - 1, column);
				}
			}
        }

        /**
         * Attempts to move focus to the cell below the currently focused cell.
         */
        @Override public void selectBelowCell() {
        	int focusedIndex = getFocusedIndex();
            if (focusedIndex != -1) {
            	int row = gridView.getRowFromIndex(focusedIndex);
            	int column = gridView.getColumnFromIndex(focusedIndex);
            	if(row != -1 && column != -1) {
            		select(row + 1, column);
            	}
            }
        }

        /**
         * Attempts to move focus to the cell to the left of the currently focused cell.
         */
        @Override public void selectLeftCell() {
        	selectPrevious();
        }

        /**
         * Attempts to move focus to the cell to the right of the the currently focused cell.
         */
        @Override public void selectRightCell() {
        	selectNext();
        }


        /* *********************************************************************
         *                                                                     *
         * Private implementation                                              *
         *                                                                     *
         **********************************************************************/

        private void updateItemCount() {
            if (gridView == null) {
                itemCount = -1;
            } else {
                List<T> items = gridView.getItems();
                itemCount = items == null ? -1 : items.size();
            }
        }

        private void updateItemsObserver(ObservableList<T> oldList, ObservableList<T> newList) {
            // update listeners
            if (oldList != null) {
                oldList.removeListener(weakItemsContentObserver);
            }
            if (newList != null) {
                newList.addListener(weakItemsContentObserver);
            }

            updateItemCount();
            updateDefaultSelection();
        }

        private void updateDefaultSelection() {
            // when the items list totally changes, we should clear out
            // the selection and focus
            int newSelectionIndex = -1;
            int newFocusIndex = -1;
            if (gridView.getItems() != null) {
                T selectedItem = getSelectedItem();
                if (selectedItem != null) {
                    newSelectionIndex = gridView.getItems().indexOf(selectedItem);
                    newFocusIndex = newSelectionIndex;
                }

                // we put focus onto the first item, if there is at least
                // one item in the list
                
                /*if (gridView.selectFirstRowByDefault && newFocusIndex == -1) {
                    newFocusIndex = gridView.getItems().size() > 0 ? 0 : -1;
                }*/
            }

            clearSelection();
            select(newSelectionIndex);
//            focus(newFocusIndex);
        }
        
        
        /***************************************************************************
         *                                                                         *
         * Ignore Methods, Because This Is Not a TableView                                                    *
         *                                                                         *
         **************************************************************************/
        
		@Override
		public boolean isSelected(int row, TableColumnBase<T, ?> column) { return false; }
		@Override
		public void select(int row, TableColumnBase<T, ?> column) {}
		@Override
		public void clearAndSelect(int row, TableColumnBase<T, ?> column) {}
		@Override
		public void clearSelection(int row, TableColumnBase<T, ?> column) {}
		@Override
		public void selectRange(int minRow, TableColumnBase<T, ?> minColumn, int maxRow, TableColumnBase<T, ?> maxColumn) {}
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
        private int itemCount = 0;

        public GridViewFocusModel(final GridView<T> gridView) {
            if (gridView == null) {
                throw new IllegalArgumentException("GridView can not be null");
            }

            this.gridView = gridView;

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

            updateItemCount();
            updateDefaultFocus();

            focusedIndexProperty().addListener(o -> {
                gridView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
            });
        }


        private void updateItemsObserver(ObservableList<T> oldList, ObservableList<T> newList) {
            // the listview items list has changed, we need to observe
            // the new list, and remove any observer we had from the old list
            if (oldList != null) oldList.removeListener(weakItemsContentListener);
            if (newList != null) newList.addListener(weakItemsContentListener);

            updateItemCount();
            updateDefaultFocus();
        }

        private final InvalidationListener itemsObserver;

        // Listen to changes in the listview items list, such that when it
        // changes we can update the focused index to refer to the new indices.
        private final ListChangeListener<T> itemsContentListener = c -> {
            updateItemCount();

            while (c.next()) {
                // looking at the first change
                int from = c.getFrom();

                if (c.wasReplaced() || c.getAddedSize() == getItemCount()) {
                    updateDefaultFocus();
                    return;
                }

                if (getFocusedIndex() == -1 || from > getFocusedIndex()) {
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

                if (added && !removed) {
                    focus(Math.min(getItemCount() - 1, getFocusedIndex() + addedSize));
                } else if (!added && removed) {
                    focus(Math.max(0, getFocusedIndex() - removedSize));
                }
            }
        };

        private WeakListChangeListener<T> weakItemsContentListener
                = new WeakListChangeListener<>(itemsContentListener);

        @Override protected int getItemCount() {
            return itemCount;
        }

        @Override protected T getModelItem(int index) {
            if (isEmpty()) return null;
            if (index < 0 || index >= itemCount) return null;

            return gridView.getItems().get(index);
        }

        private boolean isEmpty() {
            return itemCount == -1;
        }

        private void updateItemCount() {
            if (gridView == null) {
                itemCount = -1;
            } else {
                List<T> items = gridView.getItems();
                itemCount = items == null ? -1 : items.size();
            }
        }

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
        
        public void focus(int row, int column) {
        	int index = gridView.getIndexInRowColumn(row, column);
        	if(index != -1) {
        		focus(index);
        	}
        }
        
        /**
         * Tests whether the row / column at the given location currently has the
         * focus within the GridView.
         */
        public boolean isFocused(int row, int column) {
            if (column < 0 || row < 0) return false;
			int focusedIndex = getFocusedIndex();
			int focusedRow = gridView.getRowFromIndex(focusedIndex);
			int focusedColumn = gridView.getColumnFromIndex(focusedIndex);
			if(focusedRow != -1 && focusedColumn != -1) {
				return focusedRow == row && focusedColumn == column;
			}
			return false;
        }

        /**
         * Attempts to move focus to the cell above the currently focused cell.
         */
        public void focusAboveCell() {
			int focusedIndex = getFocusedIndex();
			if (focusedIndex != -1) {
				int row = gridView.getRowFromIndex(focusedIndex);
				int column = gridView.getColumnFromIndex(focusedIndex);
				if (row != -1 && column != -1) {
					focus(row - 1, column);
				}
			}
        }

        /**
         * Attempts to move focus to the cell below the currently focused cell.
         */
        public void focusBelowCell() {
        	int focusedIndex = getFocusedIndex();
            if (focusedIndex != -1) {
            	int row = gridView.getRowFromIndex(focusedIndex);
            	int column = gridView.getColumnFromIndex(focusedIndex);
            	if(row != -1 && column != -1) {
            		focus(row + 1, column);
            	}
            }
        }

        /**
         * Attempts to move focus to the cell to the left of the currently focused cell.
         */
        public void focusLeftCell() {
        	focusPrevious();
        }

        /**
         * Attempts to move focus to the cell to the right of the the currently focused cell.
         */
        public void focusRightCell() {
        	focusNext();
        }
    }
    
    /**************************************************************************
     * 
     * Public API
     * 
     **************************************************************************/
    
    public int getIndexInRowColumn(int row, int column) {
		if (row >= 0 && column >= 0) {
	    	Skin<?> skin = getSkin();
			if (skin instanceof GridViewSkin) {
				GridViewSkin<?> gskin = (GridViewSkin<?>) skin;
				int maxCellsInRow = gskin.computeMaxCellsInRow();
				int index = row * maxCellsInRow + column;
				return index < getItems().size() ? index : -1;
			}
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
                /* cell equals to null means the row is a placeholder node */
                return cell != null ?  cell : row;
            }
            default: return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
}
