/**
 * Copyright 2012 - 2013 Andy Till
 * 
 * This file is part of EstiMate.
 * 
 * EstiMate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EstiMate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EstiMate.  If not, see <http://www.gnu.org/licenses/>.
 */

package model;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.image.Image;

public class ImageCache {
    private final ConcurrentHashMap<URL, Image> cachedImages;
    
    public ImageCache() {
        cachedImages = new ConcurrentHashMap<URL, Image>(16, 0.75f, 2);
    }
    
    public Image get(URL url) {
        return cachedImages.get(url);
    }

    public Image put(URL key, Image value) {
        return cachedImages.put(key, value);
    }
}