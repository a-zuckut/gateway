/**
 * Copyright 2007-2016, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.gateway.service.turn.proxy.stun.attributes;

import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.USERNAME;

/**
 * TURN Even Port attribute as described in https://tools.ietf.org/html/rfc5766#section-14.6.
 *
 */
public class Username extends Attribute {

    private byte[] value;

    public Username(byte[] value) {
        this.value = value;
    }

    @Override
    public short getType() {
        return USERNAME.getType();
    }

    @Override
    public short getLength() {
        return (short) value.length;
    }

    @Override
    public byte[] getVariable() {
        return value;
    }
    
    public String getUsername() {
        String x = "";
        for(int i = 0; i < value.length; i++) {
            x += (char) value[i];
        }
        return x;
    }

}
