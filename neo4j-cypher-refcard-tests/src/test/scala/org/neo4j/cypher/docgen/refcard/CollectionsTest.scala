/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.docgen.refcard

import org.neo4j.cypher.{ ExecutionResult, StatisticsChecker }
import org.neo4j.cypher.docgen.RefcardTest

class CollectionsTest extends RefcardTest with StatisticsChecker {
  val graphDescription = List("A KNOWS B")
  val title = "Collections"
  val css = "general c3-3 c4-2 c5-2 c6-4"

  override def assert(name: String, result: ExecutionResult) {
    name match {
      case "returns-two" =>
        assertStats(result, nodesCreated = 0)
        assert(result.toList.size === 2)
      case "returns-one" =>
        assertStats(result, nodesCreated = 0)
        assert(result.toList.size === 1)
      case "returns-none" =>
        assertStats(result, nodesCreated = 0)
        assert(result.toList.size === 0)
    }
  }


  override def parameters(name: String): Map[String, Any] =
    name match {
      case "parameters=name" =>
        Map("value" -> "Bob")
      case "parameters=coll" =>
        Map("coll" -> List(1,2,3))
      case "parameters=range" =>
        Map("first_num" -> 1, "last_num" -> 10, "step" -> 2)
      case "parameters=subscript" =>
        Map("start_idx" -> 1, "end_idx" -> -1, "idx" -> 0)
      case "" =>
        Map()
    }

  override val properties: Map[String, Map[String, Any]] = Map(
    "A" -> Map("name" -> "Alice","coll"->Array(1,2,3)),
    "B" -> Map("name" -> "Bob","coll"->Array(1,2,3)))

  def text = """
###assertion=returns-one
RETURN

['a','b','c'] as coll

###

Literal collections are declared in square brackets.

###assertion=returns-one parameters=coll
RETURN

length({coll}) as len, {coll}[0] as value

###

Collections can be passed in as parameters.

###assertion=returns-one parameters=range
RETURN

range({first_num},{last_num},{step}) as coll

###

Range creates a collection of numbers (+step+ is optional), other functions returning collections are:
+labels+, +nodes+, +rels+, +filter+, +extract+.

###assertion=returns-one
//

MATCH (a)-[r:KNOWS*]->()
RETURN r as rels

###

Relationship identifiers of a variable length path contain a collection of relationships.

###assertion=returns-two
MATCH (node)

RETURN node.coll[0] as value, length(node.coll) as len

###

Properties can be arrays/collections of strings, numbers or booleans.

###assertion=returns-one parameters=subscript
WITH [1,2,3] as coll
RETURN

coll[{idx}] as value,
coll[{start_idx}..{end_idx}] as slice

###

Collection elements can be accessed with +idx+ subscripts in square brackets. Invalid indexes return +NULL+.
Slices can
be retrieved with intervals from +start_idx+ to +end_idx+ each of which can be omitted or negative.
Out of range elements are ignored.
"""
}
