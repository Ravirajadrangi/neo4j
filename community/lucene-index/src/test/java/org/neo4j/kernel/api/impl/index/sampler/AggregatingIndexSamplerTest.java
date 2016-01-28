/*
 * Copyright (c) 2002-2016 "Neo Technology,"
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
package org.neo4j.kernel.api.impl.index.sampler;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import org.neo4j.kernel.api.exceptions.index.IndexNotFoundKernelException;
import org.neo4j.register.Register;
import org.neo4j.register.Registers;
import org.neo4j.storageengine.api.schema.IndexSampler;

import static org.junit.Assert.assertEquals;

public class AggregatingIndexSamplerTest
{

    @Test
    public void samplePartitionedIndex() throws IndexNotFoundKernelException
    {
        List<IndexSampler> samplers = Arrays.asList( createSampler( 1 ), createSampler( 2 ) );
        AggregatingIndexSampler partitionedSampler = new AggregatingIndexSampler( samplers );

        Register.DoubleLongRegister resultRegister = Registers.newDoubleLongRegister( 0, 0 );
        long sampleIndex = partitionedSampler.sampleIndex( resultRegister );

        assertEquals( 3, sampleIndex );
        assertEquals( 3, resultRegister.readFirst() );
        assertEquals( 6, resultRegister.readSecond() );
    }

    private IndexSampler createSampler( long value )
    {
        return new TestIndexSampler( value );
    }

    private static class TestIndexSampler implements IndexSampler
    {
        private final long value;

        public TestIndexSampler( long value )
        {
            this.value = value;
        }

        @Override
        public long sampleIndex( Register.DoubleLong.Out result ) throws IndexNotFoundKernelException
        {
            result.write( value, value * 2 );
            return value;
        }
    }
}