/*
 * {{{ header & license
 * Copyright (c) 2007 Wisconsin Court System
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package com.openhtmltopdf.css.newmatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.openhtmltopdf.css.constants.CSSName;
import com.openhtmltopdf.css.constants.IdentValue;
import com.openhtmltopdf.css.constants.MarginBoxName;
import com.openhtmltopdf.css.parser.PropertyValue;
import com.openhtmltopdf.css.sheet.PropertyDeclaration;
import com.openhtmltopdf.css.sheet.StylesheetInfo;

public class PageInfo {
    private final CascadedStyle _pageStyle;
    private final Map<MarginBoxName, List<PropertyDeclaration>> _marginBoxes;

    private final List<PropertyDeclaration> _properties;
    private final List<PropertyDeclaration> _xmpPropertyList;
    private final List<PropertyDeclaration> _footnote;

    public PageInfo(
            List<PropertyDeclaration> properties,
            CascadedStyle pageStyle,
            Map<MarginBoxName, List<PropertyDeclaration>>  marginBoxes,
            List<PropertyDeclaration> footnote) {
        _properties = properties;
        _pageStyle = pageStyle;
        _marginBoxes = marginBoxes;
        _footnote = footnote;

        _xmpPropertyList = marginBoxes.remove(MarginBoxName.FS_PDF_XMP_METADATA);
    }

    public Map<MarginBoxName, List<PropertyDeclaration>> getMarginBoxes() {
        return _marginBoxes;
    }
    
    public CascadedStyle getPageStyle() {
        return _pageStyle;
    }
    
    public List<PropertyDeclaration> getProperties() {
        return _properties;
    }

    /**
     * Creates a footnote area style from footnote at-rule properties
     * for this page with display overriden to block and
     * position overriden as absolute.
     */
    public CascadedStyle createFootnoteAreaStyle() {
        List<PropertyDeclaration> overrides = Arrays.asList(
                CascadedStyle.createLayoutPropertyDeclaration(CSSName.POSITION, IdentValue.ABSOLUTE),
                CascadedStyle.createLayoutPropertyDeclaration(CSSName.DISPLAY, IdentValue.BLOCK));

        if (_footnote == null || _footnote.isEmpty()) {
            return new CascadedStyle(overrides.iterator());
        }

        List<PropertyDeclaration> all = new ArrayList<>(overrides.size() + _footnote.size());

        all.addAll(_footnote);
        all.addAll(overrides);

        return new CascadedStyle(all.iterator());
    }

    public CascadedStyle createMarginBoxStyle(MarginBoxName marginBox, boolean alwaysCreate) {
        List<PropertyDeclaration> marginProps = _marginBoxes.get(marginBox);

        if ((marginProps == null || marginProps.size() == 0) && ! alwaysCreate) {
            return null;
        }
        
        List<PropertyDeclaration> all;
        if (marginProps != null) {
            all = new ArrayList<>(marginProps.size() + 3);
            all.addAll(marginProps);    
        } else {
            all = new ArrayList<>(3);
        }
        
        all.add(CascadedStyle.createLayoutPropertyDeclaration(CSSName.DISPLAY, IdentValue.TABLE_CELL));
        all.add(new PropertyDeclaration(
                    CSSName.VERTICAL_ALIGN, 
                    new PropertyValue(marginBox.getInitialVerticalAlign()), 
                    false,
                    StylesheetInfo.USER_AGENT));
        all.add(new PropertyDeclaration(
                CSSName.TEXT_ALIGN, 
                new PropertyValue(marginBox.getInitialTextAlign()), 
                false,
                StylesheetInfo.USER_AGENT));        
                        
        
        return new CascadedStyle(all.iterator());
    }
    
    public boolean hasAny(MarginBoxName[] marginBoxes) {
        for (MarginBoxName marginBox : marginBoxes) {
            if (_marginBoxes.containsKey(marginBox)) {
                return true;
            }
        }
        
        return false;
    }

    public List<PropertyDeclaration> getXMPPropertyList() {
        return _xmpPropertyList;
    }
}
