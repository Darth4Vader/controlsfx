package org.controlsfx.control;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import impl.org.controlsfx.skin.GridRow;
import impl.org.controlsfx.skin.GridRowSkin;
import javafx.beans.NamedArg;
import javafx.scene.control.Cell;

/**
 * This class is used to represent a single row/column/cell in a GridView.
 * This is used throughout the GridView API to represent which rows/columns/cells
 * are currently selected, focused, being edited, etc. Note that this class is
 * immutable once it is created.
 *
 * <p>Because the GridView can have different
 * {@link SelectionMode selection modes}, the row and column properties in
 * TablePosition can be 'disabled' to represent an entire row or column. This is
 * done by setting the unrequired property to -1 or null.
 *
 * @param <S> The type of the items contained within the GridView (i.e. the same
 *      generic type as the S in GridView&lt;S&gt;).
 * @param <T> The type of the items contained within the GridRow.
 * @see GridView
 * @see GridRow
 * @since JavaFX 2.0
 */
public class GridViewPosition<T> extends GridViewPositionBase<GridRow<T>> {

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Constructs a TablePosition instance to represent the given row/column
     * position in the given GridView instance. Both the GridView and
     * GridRow are referenced weakly in this class, so it is possible that
     * they will be null when their respective getters are called.
     *
     * @param gridView The GridView that this position is related to.
     * @param row The row that this TablePosition is representing.
     * @param tableColumn The GridRow instance that this TablePosition represents.
     */
    @SuppressWarnings("unchecked")
	public GridViewPosition(@NamedArg("gridView") GridView<T> gridView, @NamedArg("gridRow") GridRow<T> gridRow, @NamedArg("column") int column) {
        super(gridRow, column);
        this.controlRef = new WeakReference<>(gridView);
        
        //GridRowSkin<?> skin = (GridRowSkin<?>) gridRow.getSkin();
        List<T> items = gridRow != null && (gridRow.getSkin() instanceof GridRowSkin) 
	        		? ((GridRowSkin<?>) gridRow.getSkin()).getChildren().stream()
		        		.filter(GridCell.class::isInstance)
		        		.map(node -> (GridCell<T>) node)
		        		.map(cell -> cell.getItem())
		        		.collect(Collectors.toList())
	        		: null;
        this.itemRef = new WeakReference<>(
                items != null && (column) >= 0 && column < items.size() ? items.get(column) : null);

        nonFixedRowIndex = gridView == null || gridRow == null ? -1 : gridRow.getIndex();
    }



    /* *************************************************************************
     *                                                                         *
     * Instance Variables                                                      *
     *                                                                         *
     **************************************************************************/

    private final WeakReference<GridView<T>> controlRef;
    private final WeakReference<T> itemRef;
    int fixedRowIndex = -1;
    private final int nonFixedRowIndex;

    /* *************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * The column index that this TablePosition represents in the GridView. It
     * is -1 if the GridView or GridRow instances are null at the time the class
     * is instantiated (i.e. it is computed at construction).
     */
    @Override public int getRow() {
        if (fixedRowIndex > -1) {
            return fixedRowIndex;
        }

        return nonFixedRowIndex;
    }

    /**
     * The GridView that this TablePosition is related to.
     * @return the GridView
     */
    public final GridView<T> getGridView() {
        return controlRef.get();
    }

    /** {@inheritDoc} */
    @Override public final GridRow<T> getGridRow() {
        // Forcing the return type to be GridRow<S,T>, not GridRowBase<S,T>
        return super.getGridRow();
    }

    /**
     * Returns the item that backs the {@link #getRow()} row}, at the point
     * in time when this TablePosition was created.
     */
    final T getItem() {
        return itemRef == null ? null : itemRef.get();
    }

    /**
     * Returns a string representation of this {@code TablePosition} object.
     * @return a string representation of this {@code TablePosition} object.
     */
    @Override public String toString() {
        return "GridViewPosition [ row: " + getGridRow() + ", column: " + getColumn() + ", "
                + "gridView: " + getGridView() + " ]";
    }
}
