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

import org.controlsfx.control.GridView.GridViewFocusModel;

import impl.org.controlsfx.skin.GridCellSkin;
import impl.org.controlsfx.skin.GridRow;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.AccessibleAction;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;

/**
 * A GridCell is created to represent items in the {@link GridView} 
 * {@link GridView#getItems() items list}. As with other JavaFX UI controls
 * (like {@link ListView}, {@link TableView}, etc), the {@link GridView} control
 * is virtualised, meaning it is exceedingly memory and CPU efficient. Refer to
 * the {@link GridView} class documentation for more details.
 *  
 * @see GridView
 */
public class GridCell<T> extends IndexedCell<T> {
    
    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/

    /**
     * Creates a default GridCell instance.
     */
	public GridCell() {
		getStyleClass().add("grid-cell"); //$NON-NLS-1$
		
//		itemProperty().addListener(new ChangeListener<T>() {
//            @Override public void changed(ObservableValue<? extends T> arg0, T oldItem, T newItem) {
//                updateItem(newItem, newItem == null);
//            }
//        });
		
		// TODO listen for index change and update index and item, rather than
		// listen to just item update as above. This requires the GridCell to 
		// know about its containing GridRow (and the GridRow to know its 
		// containing GridView)
		indexProperty().addListener(new InvalidationListener() {
            @Override public void invalidated(Observable observable) {
                final GridView<T> gridView = getGridView();
                if (gridView == null) return;
                
                if(getIndex() < 0) {
                    updateItem(null, true);
                    return;
                }
                T item = gridView.getItems().get(getIndex());
                
//                updateIndex(getIndex());
                updateItem(item, item == null);
            }
        });
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override protected Skin<?> createDefaultSkin() {
        return new GridCellSkin<>(this);
    }
	
	
	
	/**************************************************************************
     * 
     * Properties
     * 
     **************************************************************************/
	
	/**
     * The {@link GridView} that this GridCell exists within.
     */
    /*public SimpleObjectProperty<GridView<T>> gridViewProperty() {
        return gridView;
    }
    private final SimpleObjectProperty<GridView<T>> gridView = 
            new SimpleObjectProperty<>(this, "gridView"); //$NON-NLS-1$
    
    /**
     * Sets the {@link GridView} that this GridCell exists within.
     */
    /*public final void updateGridView(GridView<T> gridView) {
        this.gridView.set(gridView);
    }
    
    /**
     * Returns the {@link GridView} that this GridCell exists within.
     */
    /*public GridView<T> getGridView() {
        return gridView.get();
    }
    
	/**************************************************************************
     * 
     * My Custom
     * 
     **************************************************************************/
    
    // same as above, but for focus
    private final InvalidationListener focusedListener = value -> {
        updateFocus();
    };
    
    private final WeakInvalidationListener weakFocusedListener =
            new WeakInvalidationListener(focusedListener);
    
    // --- GridView
    private ReadOnlyObjectWrapper<GridView<T>> gridView;
    private void setGridView(GridView<T> value) {
        gridViewPropertyImpl().set(value);
    }
    public final GridView<T> getGridView() {
        return gridView == null ? null : gridView.get();
    }

    /**
     * The GridView associated with this TableCell.
     * @return the GridView associated with this TableCell
     */
    public final ReadOnlyObjectProperty<GridView<T>> gridViewProperty() {
        return gridViewPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<GridView<T>> gridViewPropertyImpl() {
        if (gridView == null) {
            gridView = new ReadOnlyObjectWrapper<>() {
                private WeakReference<GridView<T>> weakGridViewRef;
                @Override protected void invalidated() {
                    //GridView.TableViewSelectionModel<S> sm;
                    GridViewFocusModel<T> fm;

                    if (weakGridViewRef != null) {
                        cleanUpGridViewListeners(weakGridViewRef.get());
                    }

                    if (get() != null) {
                        /*sm = get().getSelectionModel();
                        if (sm != null) {
                            sm.getSelectedCells().addListener(weakSelectedListener);
                        }*/

                        fm = get().getFocusModel();
                        if (fm != null) {
                            fm.focusedCellProperty().addListener(weakFocusedListener);
                        }
                        
                        /*
                        get().editingCellProperty().addListener(weakEditingListener);
                        get().getVisibleLeafColumns().addListener(weakVisibleLeafColumnsListener);
                        */
                        
                        weakGridViewRef = new WeakReference<>(get());
                    }

                    //updateColumnIndex();
                }

                @Override public Object getBean() {
                    return GridCell.this;
                }

                @Override public String getName() {
                    return "gridView";
                }
            };
        }
        return gridView;
    }
    
    /**
     * Sets the {@link GridView} that this GridCell exists within.
     */
    public final void updateGridView(GridView<T> gridView) {
        this.gridView.set(gridView);
    }
    
    private void cleanUpGridViewListeners(GridView<T> tableView) {
        if (tableView != null) {
            /*TableView.TableViewSelectionModel<S> sm = tableView.getSelectionModel();
            if (sm != null) {
                sm.getSelectedCells().removeListener(weakSelectedListener);
            }*/

            GridViewFocusModel<T> fm = tableView.getFocusModel();
            if (fm != null) {
                fm.focusedCellProperty().removeListener(weakFocusedListener);
            }
            
            /*
            tableView.editingCellProperty().removeListener(weakEditingListener);
            tableView.getVisibleLeafColumns().removeListener(weakVisibleLeafColumnsListener);
            */
        }
    }
    /*
    @Override void indexChanged(int oldIndex, int newIndex) {
        super.indexChanged(oldIndex, newIndex);

        // Ideally we would just use the following two lines of code, rather
        // than the updateItem() call beneath, but if we do this we end up with
        // RT-22428 where all the columns are collapsed.
        // itemDirty = true;
        // requestLayout();
        updateItem(oldIndex);
        updateSelection();
        updateFocus();

        // Fix for JDK-8150525
        updateEditing();
    }
    */
    private void updateFocus() {
        final boolean isFocused = isFocused();
        /*if (! isInCellSelectionMode()) {
            if (isFocused) {
                setFocused(false);
            }
            return;
        }*/

        final GridView<T> gridView = getGridView();
        final GridRow<T> gridRow = getGridRow();
        final int index = getIndex();
        if (index == -1 || gridView == null || gridRow == null) return;

        final GridViewFocusModel<T> fm = gridView.getFocusModel();
        if (fm == null) {
            setFocused(false);
            return;
        }

        setFocused(fm.isFocused(getGridRow(), index));
    }
    
    // --- GridRow
    /**
     * The GridRow that this GridCell currently finds itself placed within.
     * The GridRow may be null early in the TableCell lifecycle, in the period
     * between the TableCell being instantiated and being set into an owner
     * GridRow.
     */
    private ReadOnlyObjectWrapper<GridRow<T>> gridRow = new ReadOnlyObjectWrapper<>(this, "tableRow");
    private void setGridRow(GridRow<T> value) { gridRow.set(value); }
    public final GridRow<T> getGridRow() { return gridRow.get(); }
    public final ReadOnlyObjectProperty<GridRow<T>> gridRowProperty() { return gridRow;  }
    
    /**
     * Updates the GridRow associated with this TableCell.
     *
     * Note: This function is intended to be used by experts, primarily
     *       by those implementing new Skins. It is not common
     *       for developers or designers to access this function directly.
     * @param tableRow the TableRow associated with this TableCell
     */
    public final void updateGridRow(GridRow<T> gridRow) {
        this.setGridRow(gridRow);
    }
    
    /** {@inheritDoc} */
    @Override
    public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
        switch (action) {
            case REQUEST_FOCUS: {
            	System.out.println("Batman");
            	GridView<T> gridView = getGridView();
                if (gridView != null) {
                	GridViewFocusModel<T> fm = gridView.getFocusModel();
                    if (fm != null) {
                        fm.focus(getIndex());
                    }
                }
                break;
            }
            default: super.executeAccessibleAction(action, parameters);
        }
    }
}