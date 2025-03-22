/**
 * Copyright (c) 2013, 2018 ControlsFX
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
package impl.org.controlsfx.skin;

import java.lang.ref.WeakReference;

import org.controlsfx.control.GridView;
import org.controlsfx.control.GridView.GridViewFocusModel;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;

/**
 * A GridRow is a container for {@link org.controlsfx.control.GridCell}, and represents a single
 * row inside a {@link GridView}.
 */
public class GridRow<T> extends IndexedCell<T>{


    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/	

    /**
     * 
     */
    public GridRow() {
        super();
        getStyleClass().add("grid-row"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override protected Skin<?> createDefaultSkin() {
        return new GridRowSkin<>(this);
    }



    /**************************************************************************
     * 
     * Properties
     * 
     **************************************************************************/

    /**
     * The {@link GridView} that this GridRow exists within.
     */
    /*public SimpleObjectProperty<GridView<T>> gridViewProperty() {
        return gridView;
    }
    private final SimpleObjectProperty<GridView<T>> gridView = 
            new SimpleObjectProperty<>(this, "gridView"); //$NON-NLS-1$
    
    /**
     * Sets the {@link GridView} that this GridRow exists within.
     */
    /*public final void updateGridView(GridView<T> gridView) {
        this.gridView.set(gridView);
    }
    
    /**
     * Returns the {@link GridView} that this GridRow exists within.
     */
    /*public GridView<T> getGridView() {
        return gridView.get();
    }*/



    @Override
    public void updateIndex(int i) {
        super.updateIndex(i);
        // Fixes #879 (https://bitbucket.org/controlsfx/controlsfx/issues/879)
        // When VirtualFlow.setCellIndex is called GridRow should update its cells
        // even when the index did not change.
        GridRowSkin<?> skin = (GridRowSkin<?>) getSkin();
        if (skin != null) {
            skin.updateCells();
        }

        // We need to do this to allow for mouse wheel scrolling,
        // as the GridRow has to report that it is non-empty (which
        // is the second argument going into updateItem).
        updateItem(null, getIndex() == -1);
    }
    
	@Override
    protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		updateFocus();
    }
    
    // Same as selectedListener, but this time for focus events
    private final InvalidationListener focusedListener = valueModel -> {
        updateFocus();
    };
    
    private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(focusedListener);
    
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
                private WeakReference<GridView<T>> weakTableViewRef;
                @Override protected void invalidated() {
                    //TableView.TableViewSelectionModel<T> sm;
                    GridViewFocusModel<T> fm;

                    if (weakTableViewRef != null) {
                        GridView<T> oldTableView = weakTableViewRef.get();
                        if (oldTableView != null) {
                            /*sm = oldTableView.getSelectionModel();
                            if (sm != null) {
                                sm.getSelectedIndices().removeListener(weakSelectedListener);
                            }*/

                            fm = oldTableView.getFocusModel();
                            if (fm != null) {
                                fm.focusedCellProperty().removeListener(weakFocusedListener);
                            }

                            //oldTableView.editingCellProperty().removeListener(weakEditingListener);
                        }

                        weakTableViewRef = null;
                    }

                    GridView<T> tableView = getGridView();
                    if (tableView != null) {
                        /*sm = tableView.getSelectionModel();
                        if (sm != null) {
                            sm.getSelectedIndices().addListener(weakSelectedListener);
                        }*/

                        fm = tableView.getFocusModel();
                        if (fm != null) {
                            fm.focusedCellProperty().addListener(weakFocusedListener);
                        }

                        //tableView.editingCellProperty().addListener(weakEditingListener);

                        weakTableViewRef = new WeakReference<>(get());
                    }
                }

                @Override
                public Object getBean() {
                    return GridRow.this;
                }

                @Override
                public String getName() {
                    return "gridView";
                }
            };
        }
        return gridView;
    }
    
    private void updateFocus() {
        if (getIndex() == -1) return;

        GridView<T> table = getGridView();
        if (table == null) return;

        //TableView.TableViewSelectionModel<T> sm = table.getSelectionModel();
        GridView.GridViewFocusModel<T> fm = table.getFocusModel();
        if (/*sm == null ||*/ fm == null) return;

        boolean isFocused = ! /*sm.isCellSelectionEnabled() &&*/ fm.isFocused(getIndex());
        System.out.println("Row Updates Focus " + getIndex());
        setFocused(isFocused);
    }
    
    public final void updateGridView(GridView<T> gridRow) {
        this.setGridView(gridRow);
    }
}
