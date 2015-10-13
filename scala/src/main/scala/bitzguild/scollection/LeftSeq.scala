/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2001-2014, Kevin Sven Berg. All rights reserved.
 *
 * This package is part of the Bitzguild Distribution
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ***** END LICENSE BLOCK ***** */

package bitzguild.scollection

import scala.collection.{immutable, GenSeq, IndexedSeq}
import scala.collection.mutable.ArrayBuffer
import scala.collection.generic.CanBuildFrom

// ------------------------------------------------------------------------------------
// LeftSeq Interface
// ------------------------------------------------------------------------------------

 /**
  * A reverse index sequence that supports snapshot views
  * and ability to create a compatible/similar storage instance.
  *
  * @tparam A element type
  */
trait LeftSeq[A] extends scala.collection.IndexedSeq[A] {
  def firstView(lookback: Int) : LeftView[A]
  def view(lookback: Int) : LeftView[A]
  def another : MutableLeftSeq[A]
}

 /**
  * Reverse index sequence representing a 'snapshot' into parent data
  *
  * @tparam A element type
  */
trait LeftView[A] extends LeftSeq[A] {
  def next : LeftView[A]
  def hasNext : Boolean
}

 /**
  * Narrowly defined mutability operations. LeftSeq collections can only be modified by appending new data to the end.
  * The 'end' is always latest, and referenced from index zero.
  *
  * @tparam A element type
  */
trait MutableLeftSeq[A] extends LeftSeq[A] {
  def +=(elem: A): LeftSeq[A]
  def ++=(col: Traversable[A]) : LeftSeq[A]
}

