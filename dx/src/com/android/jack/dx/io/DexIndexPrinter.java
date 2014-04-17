/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.jack.dx.io;

import com.android.jack.dx.dex.TableOfContents;

import java.io.File;
import java.io.IOException;

/**
 * Executable that prints all indices of a dex file.
 */
public final class DexIndexPrinter {
  private final DexBuffer dexBuffer;
  private final TableOfContents tableOfContents;

  public DexIndexPrinter(File file) throws IOException {
    this.dexBuffer = new DexBuffer(file);
    this.tableOfContents = dexBuffer.getTableOfContents();
  }

  private void printMap() {
    for (TableOfContents.Section section : tableOfContents.sections) {
      if (section.off != -1) {
        System.out.println("section " + Integer.toHexString(section.type) + " off="
            + Integer.toHexString(section.off) + " size=" + Integer.toHexString(section.size)
            + " byteCount=" + Integer.toHexString(section.byteCount));
      }
    }
  }

  private void printStrings() {
    int index = 0;
    for (String string : dexBuffer.strings()) {
      System.out.println("string " + index + ": " + string);
      index++;
    }
  }

  private void printTypeIds() {
    int index = 0;
    for (Integer type : dexBuffer.typeIds()) {
      System.out.println("type " + index + ": " + dexBuffer.strings().get(type));
      index++;
    }
  }

  private void printProtoIds() {
    int index = 0;
    for (ProtoId protoId : dexBuffer.protoIds()) {
      System.out.println("proto " + index + ": " + protoId);
      index++;
    }
  }

  private void printFieldIds() {
    int index = 0;
    for (FieldId fieldId : dexBuffer.fieldIds()) {
      System.out.println("field " + index + ": " + fieldId);
      index++;
    }
  }

  private void printMethodIds() {
    int index = 0;
    for (MethodId methodId : dexBuffer.methodIds()) {
      System.out.println("methodId " + index + ": " + methodId);
      index++;
    }
  }

  private void printTypeLists() {
    if (tableOfContents.typeLists.off == -1) {
      System.out.println("No type lists");
      return;
    }
    DexBuffer.Section in = dexBuffer.open(tableOfContents.typeLists.off);
    for (int i = 0; i < tableOfContents.typeLists.size; i++) {
      int size = in.readInt();
      System.out.print("Type list i=" + i + ", size=" + size + ", elements=");
      for (int t = 0; t < size; t++) {
        System.out.print(" " + dexBuffer.typeNames().get(in.readShort()));
      }
      if (size % 2 == 1) {
        in.readShort(); // retain alignment
      }
      System.out.println();
    }
  }

  private void printClassDefs() {
    int index = 0;
    for (ClassDef classDef : dexBuffer.classDefs()) {
      System.out.println("class def " + index + ": " + classDef);
      index++;
    }
  }

  public static void main(String[] args) throws IOException {
    DexIndexPrinter indexPrinter = new DexIndexPrinter(new File(args[0]));
    indexPrinter.printMap();
    indexPrinter.printStrings();
    indexPrinter.printTypeIds();
    indexPrinter.printProtoIds();
    indexPrinter.printFieldIds();
    indexPrinter.printMethodIds();
    indexPrinter.printTypeLists();
    indexPrinter.printClassDefs();
  }
}
