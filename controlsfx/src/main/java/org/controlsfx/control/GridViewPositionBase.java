package org.controlsfx.control;

import java.lang.ref.WeakReference;

import impl.org.controlsfx.skin.GridRow;

/**
 * This class is used to represent a single column/column/cell in a table. Concrete
 * subclasses of this abstract class are used in the {@link GridView} and
 * {@link TreeGridView} APIs to represent which columns/columns/cells
 * are currently selected, focused, being edited, etc. Note that this class is
 * immutable once it is created.
 *
 * <p>Because the GridView and TreeGridView controls can have different
 * {@link SelectionMode selection modes}, the column and column properties in
 * GridViewPositionBase can be 'disabled' to represent an entire column or column. This is
 * done by setting the unrequired property to -1 or null.
 *
 * @param <TC> the type of the GridRow
 * @see GridViewPosition
 * @see TreeGridViewPosition
 * @since JavaFX 8.0
 */
public abstract class GridViewPositionBase<TC extends GridRow> {

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Constructs a GridViewPositionBase instance to represent the given column/column
     * position in the underlying table instance (which is not part of the
     * abstract GridViewPositionBase class, but is part of concrete subclasses such
     * as {@link GridViewPosition} and {@link TreeGridViewPosition}). In all cases,
     * all fields inside GridViewPositionBase instances are referenced weakly so as
     * to prevent memory leaks. This means that it is possible (but unlikely)
     * that the get methods will return null.
     *
     * @param gridRow The GridRow instance that this GridViewPosition represents.
     *      * @param column The column that this GridViewPosition is representing.
     */
    protected GridViewPositionBase(TC gridRow, int column) {
        this.column = column;
        this.gridRowRef = new WeakReference<>(gridRow);
    }



    /* *************************************************************************
     *                                                                         *
     * Instance Variables                                                      *
     *                                                                         *
     **************************************************************************/

    private final WeakReference<TC> gridRowRef;
    private final int column;


    
    /* *************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * The column that this GridViewPosition represents in the GridView.
     * @return the column that this GridViewPosition represents in the GridView
     */
    public int getColumn() {
        return column;
    }

    /**
     * The row index that this GridViewPosition represents in the GridView. It
     * is -1 if the GridView or GridRow instances are null.
     * @return the row index that this GridViewPosition represents in the
     * GridView
     */
    public abstract int getRow();

    /**
     * The GridRow that this GridViewPosition represents in the GridView.
     * @return the GridRow that this GridViewPosition represents in the GridView
     */
    public TC getGridRow() {
        return gridRowRef.get();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is equal to the {@code obj} argument; {@code false} otherwise.
     */
    @Override public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final GridViewPositionBase other = (GridViewPositionBase) obj;
        if (this.column != other.column) {
            return false;
        }
        TC gridRow = getGridRow();
        GridRow otherGridRow = other.getGridRow();
        if (gridRow != otherGridRow && (gridRow == null || !gridRow.equals(otherGridRow))) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this {@code GridViewPosition} object.
     * @return a hash code for this {@code GridViewPosition} object.
     */
    @Override public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.column;
        GridRow gridRow = getGridRow();
        hash = 79 * hash + (gridRow != null ? gridRow.hashCode() : 0);
        return hash;
    }
}
