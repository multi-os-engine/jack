/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.sched.marker;

import com.android.sched.item.MarkerOrComponent;
import com.android.sched.item.TagOrMarker;
import com.android.sched.item.TagOrMarkerOrComponent;
import com.android.sched.item.onlyfor.Internal;
import com.android.sched.item.onlyfor.OnlyFor;

import javax.annotation.Nonnull;

/**
 * An object linked with <i>data</i>. Can be added, removed or modified.
 * The {@link ValidOn} or {@link DynamicValidOn} annotations must be used to specify on which type
 * of <i>data</i> the Marker can be applied.
 */
@OnlyFor(Internal.class)
public interface Marker extends TagOrMarker, TagOrMarkerOrComponent, MarkerOrComponent {
  /**
   * Returns a marker with the same properties when this marker needs to be cloned. According
   * to the implementation of the marker, the same instance may be returned, or a clone.
   * <p>
   * When a "marked" object is cloned, all its markers must be cloned too. Therefore the new
   * "cloned" object is marked with all the "cloned" markers. It is possible to return the same
   * instance if cloning this marker makes no sense regarding its purpose (like a marker that
   * never changes its state).
   * </p>
   *
   * @return this marker or a new one if needed
   */
  @Nonnull
  public Marker cloneIfNeeded();
}
