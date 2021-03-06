/*
 * Copyright (c) 2019 Beyond the lines
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.ByteArrayOutputStream

import com.google.protobuf.{CodedInputStream, CodedOutputStream}

package object pbdirect {
  implicit class PBWriterOps[A](private val a: A) extends AnyVal {
    def toPB(implicit writer: PBWriter[A]): Array[Byte] = {
      val out   = new ByteArrayOutputStream()
      val pbOut = CodedOutputStream.newInstance(out)
      writer.writeTo(1, a, pbOut)
      pbOut.flush()
      val bytes = out.toByteArray
      // remove the tag and return the content
      val input = CodedInputStream.newInstance(bytes)
      input.readTag()
      input.readByteArray()
    }
  }
  implicit class PBParserOps(private val bytes: Array[Byte]) extends AnyVal {
    def pbTo[A](implicit reader: PBParser[A]): A = {
      // wraps the bytes into a protobuf single field message
      val out   = new ByteArrayOutputStream()
      val pbOut = CodedOutputStream.newInstance(out)
      pbOut.writeByteArray(1, bytes)
      pbOut.flush()
      reader.parse(1, out.toByteArray)
    }
  }
}
