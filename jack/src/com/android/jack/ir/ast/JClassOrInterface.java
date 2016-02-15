/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.jack.ir.ast;

import com.android.jack.lookup.JMethodLookupException;
import com.android.sched.item.Description;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Class or interface.
 */
@Description("Class or interface")
public interface JClassOrInterface extends JReferenceType, HasEnclosingPackage {

  /**
   * Search a corresponding {@link JMethodId} in this {@code JClassOrInterface} or in its super
   * classes or interfaces. If none can be found then a corresponding {@link JMethodId} is
   * created in this {@link JClassOrInterface} and returned.
   * @param name name of the method.
   * @param argsType arguments types of the method.
   * @param kind kind of the searched method id.
   * @param returnType return type of the searched method id.
   * @return a corresponding {@link JMethodId}.
   */
  @Nonnull
  JMethodId getOrCreateMethodId(
      @Nonnull String name,
      @Nonnull List<? extends JType> argsType,
      @Nonnull MethodKind kind,
      @Nonnull JType returnType);

  /**
   * Search a corresponding {@link JMethodId} in this {@link JClassOrInterface} or in its super
   * classes or interfaces.
   * @param name name of the method.
   * @param argsType arguments types of the method.
   * @param kind kind of the searched method id.
   * @param returnType return type of the searched method id.
   * @return the found {@link JMethodId}.
   * @throws JMethodLookupException if no corresponding {@link JMethodId} can be found.
   */
  @Nonnull
  JMethodId getMethodId(
      @Nonnull String name,
      @Nonnull List<? extends JType> argsType,
      @Nonnull MethodKind kind,
      @Nonnull JType returnType)
      throws JMethodLookupException;

  /**
   * Search a corresponding {@link JMethodIdWide} in this {@code JClassOrInterface} or in its super
   * classes or interfaces. If none can be found then a corresponding {@link JMethodIdWide} is
   * created in this {@link JClassOrInterface} and returned.
   * @param name name of the method.
   * @param argsType arguments types of the method.
   * @param kind kind of the searched method id.
   * @return a corresponding {@link JMethodIdWide}.
   */
  @Nonnull
  JMethodIdWide getOrCreateMethodIdWide(
      @Nonnull String name,
      @Nonnull List<? extends JType> argsType,
      @Nonnull MethodKind kind);

  /**
   * Search a corresponding {@link JMethodIdWide} in this {@code JClassOrInterface} or in its super
   * classes or interfaces.
   * @param name name of the method.
   * @param argsType arguments types of the method.
   * @param kind kind of the searched method id.
   * @return the found {@link JMethodIdWide}.
   * @throws JMethodLookupException if no corresponding {@link JMethodIdWide} can be found.
   */
  @Nonnull
  JMethodIdWide getMethodIdWide(
      @Nonnull String name,
      @Nonnull List<? extends JType> argsType,
      @Nonnull MethodKind kind)
      throws JMethodLookupException;

  /**
   * Search a corresponding {@code JFieldId} in this {@code JClassOrInterface} or in its super
   * classes or interfaces. If none can be found then a corresponding {@link JFieldId} is
   * created in this {@code JClassOrInterface} and returned.
   * @param name name of the field.
   * @param type declared type of the field.
   * @param kind kind of the searched field id.
   * @return a corresponding {@code JFieldId}.
   */
  @Nonnull
  JFieldId getOrCreateFieldId(@Nonnull String name, @Nonnull JType type,
      @Nonnull FieldKind kind);

  /**
   * Search a corresponding {@code JFieldId} in this {@code JClassOrInterface} or in its super
   * classes or interfaces.
   * @param name name of the field.
   * @param type declared type of the field.
   * @param kind kind of the searched field id.
   * @return a corresponding {@code JFieldId}.
   * @throws JFieldLookupException if no corresponding {@code JFieldId} can be found.
   */
  @Nonnull
  JFieldId getFieldId(@Nonnull String name, @Nonnull JType type, @Nonnull FieldKind kind)
      throws JFieldLookupException;

  /**
   * Get the primitive type that can be wrapped by this type if any.
   * @return null if this class is not a wrapper type.
   */
  @CheckForNull
  public JPrimitiveType getWrappedType();
}
