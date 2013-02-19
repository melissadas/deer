/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.geolift.TMP;

/**
 *
 * @author ngonga
 */
public class NamedEntity implements Comparable {

    public String label;
    public int offset;
    public String type;

    /** Creates a new entity and ensures that it can be disambiguated from entities
     * with the same label by also storing the offset of the said entity
     * @param _label Label of the entity
     * @param _offset Offset with the text
     */
    public NamedEntity(String _label, int _offset) {
        label = _label;
        offset = _offset;
        type = null;
    }

    public NamedEntity(String _label, String _type, int _offset) {
        label = _label;
        offset = _offset;
        type = _type;
    }

    /** Implements comparison
     * 
     * @param o Other named entity to compare with
     * @return 0 if the entities are the same, else 1
     */
//    @Override
    public int compareTo(Object o) {
        NamedEntity e = (NamedEntity) o;
        if (e.offset == offset) {
            if(e.label.length() == label.length())
            return 0;
            if(e.label.length() > label.length())
                return 1;
            else return -1;
        }         
        else {
            if(offset > e.offset)
            return -1;
            else return 1;
        }
    }

    /** Returns the string representation of a named entity
     * 
     * @return String representation of a named entity
     */
    public String toString() {
        if (type == null) {
            return "(" + label + ", " + offset + ")";
        } else {
            return "(" + label + ", " + offset + ", " + type + ")";
        }
    }
}