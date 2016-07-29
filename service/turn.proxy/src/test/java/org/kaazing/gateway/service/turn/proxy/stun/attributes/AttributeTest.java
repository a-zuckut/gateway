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

import static org.kaazing.gateway.service.turn.proxy.stun.StunAttributeFactory.CredentialType.SHORT_TERM;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.DONT_FRAGMENT;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.EVEN_PORT;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.FINGERPRINT;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.LIFETIME;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.MAPPED_ADDRESS;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.MESSAGE_INTEGRITY;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.NONCE;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.REALM;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.RESERVATION_TOKEN;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.SOFTWARE;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.USERNAME;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.XOR_MAPPED_ADDRESS;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.XOR_PEER_ADDRESS;
import static org.kaazing.gateway.service.turn.proxy.stun.attributes.AttributeType.XOR_RELAY_ADDRESS;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaazing.gateway.service.turn.proxy.stun.StunAttributeFactory;

public class AttributeTest {

    private final StunAttributeFactory factory = new StunAttributeFactory(SHORT_TERM);
    private static byte[] IPV4_ADDR_1, IPV4_ADDR_2, IPV6_ADDR_1, IPV6_ADDR_2;

    @BeforeClass
    public static void init() throws UnknownHostException {
        IPV4_ADDR_1 = Inet4Address.getByName("127.0.0.1").getAddress();
        IPV4_ADDR_2 = Inet4Address.getByName("255.255.255.255").getAddress();
        IPV6_ADDR_1 = Inet6Address.getByName("2001:0db8:85a3:0000:0000:8a2e:0370:7334").getAddress();
        IPV6_ADDR_2 = Inet6Address.getByName("5555:5555:5555:5555:5555:5555:5555:5555").getAddress();
    }

    @Test
    public void mappedAddressIpv4() {
        // Construct Attribute
        short type = MAPPED_ADDRESS.getType();
        short length = 4 + (32 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x01, (short) 8080, IPV4_ADDR_1);

        // Assert constructed
        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        // Test Mapped Attribute setters
        MappedAddress mappedAddressAttribute = (MappedAddress) attr;
        mappedAddressAttribute.setAddress(IPV4_ADDR_2);
        mappedAddressAttribute.setPort(8000);

        ByteBuffer newValue = constructBytesAddress((byte) 0x01, (short) 8000, IPV4_ADDR_2);

        // Assert Mapped Attribute Characteristics ok
        Assert.assertEquals(mappedAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(mappedAddressAttribute.getVariable(), newValue.array()));

        // Assert Attribute ok
        Assert.assertEquals(mappedAddressAttribute.getLength(), length);
        Assert.assertEquals(mappedAddressAttribute.getAddress(), IPV4_ADDR_2);

    }

    @Test
    public void mappedAddressIpv6() throws UnknownHostException {
        short type = MAPPED_ADDRESS.getType();
        short length = 4 + (128 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x02, (short) 8080, IPV6_ADDR_1);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        MappedAddress mappedAddressAttribute = (MappedAddress) attr;
        mappedAddressAttribute.setAddress(IPV6_ADDR_2);
        mappedAddressAttribute.setPort(8000);

        ByteBuffer newValue = constructBytesAddress((byte) 0x02, (short) 8000, IPV6_ADDR_2);

        Assert.assertEquals(mappedAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(mappedAddressAttribute.getVariable(), newValue.array()));

        Assert.assertEquals(mappedAddressAttribute.getLength(), length);
        Assert.assertEquals(mappedAddressAttribute.getAddress(), IPV6_ADDR_2);

    }

    @Test
    public void noopAttribute() {
        // Random type 0x0030
        short type = 0x0030;
        short length = 8;
        ByteBuffer buf = ByteBuffer.allocate(length);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

    }

    @Test
    public void username() {
        short type = USERNAME.getType();
        String exampleUsername = "joe";
        byte[] username = exampleUsername.getBytes();
        short length = (short) username.length;
        
        Attribute attr = new Username(username);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), username));
        
        Assert.assertEquals(((Username)attr).getUsername(), exampleUsername);
    }

    @Test
    public void messageIntegrity() {
        // We will need short term and long term, probably do two class implementations.
        // Factory will take a config
        // example: "user:realm:pass"
        // hash: "0x8493fbc53ba582fb4c044c456bdc40eb"
        short type = MESSAGE_INTEGRITY.getType();
        short length = 16;
        byte[] key = new byte[]{(byte) 0x84, (byte) 0x93, (byte) 0xfb, (byte) 0xc5, (byte) 0x3b, (byte) 0xa5, (byte) 0x82,
                (byte) 0xfb, (byte) 0x4c, (byte) 0x04, (byte) 0x4c, (byte) 0x45, (byte) 0x6b, (byte) 0xdc, (byte) 0x40,
                (byte) 0xeb};
        Attribute attr = factory.get(type, length, key);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), key));

        MessageIntegrity mesInteg = (MessageIntegrity) attr;
        Assert.assertEquals(mesInteg.getLength(), length);
        Assert.assertEquals(mesInteg.getType(), type);
        Assert.assertTrue(Arrays.equals(mesInteg.getVariable(), key));

        // TODO: Decoding of the key. @MessageIntegrity
    }

    @Test
    public void errorCode() {
        Assume.assumeTrue("Not implemented", true);
    }

    @Test
    public void unknownErrorCode() {
        Assume.assumeTrue("Not implemented", true);
    }

    @Test
    public void realm() {
        short type = REALM.getType();
        String exampleRealm = "example.com";
        byte[] realm = exampleRealm.getBytes();
        short length = (short) realm.length;
        
        Attribute attr = new Realm(realm);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), realm));
        
        Assert.assertEquals(((Realm)attr).getRealm(), exampleRealm);
    }

    @Test
    public void nonce() {
        short type = NONCE.getType();
        String exampleNonce = "adl7W7PeDU4hKE72jdaQvbAMcr6h39sm";
        byte[] nonce = exampleNonce.getBytes();
        short length = (short) nonce.length;
        
        Attribute attr = new Nonce(nonce);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), nonce));
        
        Assert.assertEquals(((Nonce)attr).getNonce(), exampleNonce);
    }

    @Test
    public void xorMappedAddressIpv4() {
        short type = XOR_MAPPED_ADDRESS.getType();
        short length = 4 + (32 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x01, (short) 8080, IPV4_ADDR_1);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        XorMappedAddress xorMappedAddressAttribute = (XorMappedAddress) attr;
        Assert.assertEquals(xorMappedAddressAttribute.getPort(), 0x3e82);
        Assert.assertTrue(Arrays.equals(xorMappedAddressAttribute.getAddress(), new byte[]{0x5e, 0x12, (byte) 0xa4, 0x43}));

        xorMappedAddressAttribute.setPort(xorMappedAddressAttribute.xorWithMagicCookie((short) 8000));
        xorMappedAddressAttribute.setAddress(xorMappedAddressAttribute.xorWithMagicCookie(IPV4_ADDR_2));

        ByteBuffer newValue = constructBytesAddress((byte) 0x01, (short) 8000, IPV4_ADDR_2);

        Assert.assertEquals(xorMappedAddressAttribute.getLength(), length);
        Assert.assertEquals(xorMappedAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(xorMappedAddressAttribute.getVariable(), newValue.array()));
    }

    @Test
    public void xorMappedAddressIpv6() throws UnknownHostException {
        short type = XOR_MAPPED_ADDRESS.getType();
        short length = 4 + (128 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x02, (short) 8080, IPV6_ADDR_1);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        XorMappedAddress xorMappedAddressAttribute = (XorMappedAddress) attr;
        xorMappedAddressAttribute.setAddress(xorMappedAddressAttribute.xorWithMagicCookie(IPV6_ADDR_2));
        xorMappedAddressAttribute.setPort(xorMappedAddressAttribute.xorWithMagicCookie((short) 8000));

        ByteBuffer newValue = constructBytesAddress((byte) 0x02, (short) 8000, IPV6_ADDR_2);

        Assert.assertEquals(xorMappedAddressAttribute.getLength(), length);
        Assert.assertEquals(xorMappedAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(xorMappedAddressAttribute.getVariable(), newValue.array()));
    }

    @Test
    public void software() {
        short type = SOFTWARE.getType();
        String exampleSoftware = "Manufacturer: version";
        byte[] software = exampleSoftware.getBytes();
        short length = (short) software.length;
        
        Attribute attr = new Software(software);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), software));
        
        Assert.assertEquals(((Software)attr).getSoftware(), exampleSoftware);
    }

    @Test
    public void alternativeServer() {
        // TODO Implementation for both IPv4 and IPv6 sample class created.
        Assume.assumeTrue("Not implemented", true);
    }

    @Test
    public void fingerprint() {
        short type = FINGERPRINT.getType();
        short length = 16;
        ByteBuffer buf = ByteBuffer.allocate(length);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        Fingerprint fingerprint = (Fingerprint) attr;

        Assert.assertEquals(fingerprint.getLength(), length);
        Assert.assertEquals(fingerprint.getType(), type);
        Assert.assertTrue(Arrays.equals(fingerprint.getVariable(), buf.array()));

        // TODO: Decoding of the key. Writing a message to get a fingerprint to test. Or just a dumby fingerprint.
    }

    @Test
    public void channelNumber() {
        short type = LIFETIME.getType();
        short exampleChannelNumber = 3000;
        byte[] channelNumber = {0x0b, (byte) 0xb8}; 
        short length = 2;
        
        Attribute attr = new ChannelNumber(channelNumber);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), channelNumber));
        
        Assert.assertEquals(((ChannelNumber)attr).getChannelNumber(), exampleChannelNumber);
    }

    @Test
    public void lifetime() {
        short type = LIFETIME.getType();
        int exampleLifetime = 3600;
        byte[] lifetime = new byte[] {0x00, 0x00, 0x0e, 0x10};
        short length = 4;
        
        Attribute attr = new Lifetime(lifetime);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), lifetime));
        
        Assert.assertEquals(((Lifetime)attr).getLifetime(), exampleLifetime);
    }

    @Test
    public void xorPeerAddressIPv4() {
        short type = XOR_PEER_ADDRESS.getType();
        short length = 4 + (32 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x01, (short) 8080, IPV4_ADDR_1);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        XorPeerAddress xorPeerAddressAttribute = (XorPeerAddress) attr;
        Assert.assertEquals(xorPeerAddressAttribute.getPort(), 0x3e82);
        Assert.assertTrue(Arrays.equals(xorPeerAddressAttribute.getAddress(), new byte[]{0x5e, 0x12, (byte) 0xa4, 0x43}));

        xorPeerAddressAttribute.setPort(xorPeerAddressAttribute.xorWithMagicCookie((short) 8000));
        xorPeerAddressAttribute.setAddress(xorPeerAddressAttribute.xorWithMagicCookie(IPV4_ADDR_2));

        ByteBuffer newValue = constructBytesAddress((byte) 0x01, (short) 8000, IPV4_ADDR_2);

        Assert.assertEquals(xorPeerAddressAttribute.getLength(), length);
        Assert.assertEquals(xorPeerAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(xorPeerAddressAttribute.getVariable(), newValue.array()));
    }

    @Test
    public void xorPeerAddressIPv6() throws UnknownHostException {
        short type = XOR_PEER_ADDRESS.getType();
        short length = 4 + (128 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x02, (short) 8080, IPV6_ADDR_1);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        XorPeerAddress xorPeerAddressAttribute = (XorPeerAddress) attr;
        xorPeerAddressAttribute.setAddress(xorPeerAddressAttribute.xorWithMagicCookie(IPV6_ADDR_2));
        xorPeerAddressAttribute.setPort(xorPeerAddressAttribute.xorWithMagicCookie((short) 8000));

        ByteBuffer newValue = constructBytesAddress((byte) 0x02, (short) 8000, IPV6_ADDR_2);
        Assert.assertEquals(xorPeerAddressAttribute.getLength(), length);
        Assert.assertEquals(xorPeerAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(xorPeerAddressAttribute.getVariable(), newValue.array()));
    }

    @Test
    public void data() {
        Assume.assumeTrue("Not implemented", true);
    }

    @Test
    public void xorRelayAddressIPv4() {
        short type = XOR_RELAY_ADDRESS.getType();
        short length = 4 + (32 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x01, (short) 8080, IPV4_ADDR_1);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        XorRelayAddress xorMappedAddressAttribute = (XorRelayAddress) attr;
        Assert.assertEquals(xorMappedAddressAttribute.getPort(), 0x3e82);
        Assert.assertTrue(Arrays.equals(xorMappedAddressAttribute.getAddress(), new byte[]{0x5e, 0x12, (byte) 0xa4, 0x43}));

        xorMappedAddressAttribute.setPort(xorMappedAddressAttribute.xorWithMagicCookie((short) 8000));
        xorMappedAddressAttribute.setAddress(xorMappedAddressAttribute.xorWithMagicCookie(IPV4_ADDR_2));

        ByteBuffer newValue = constructBytesAddress((byte) 0x01, (short) 8000, IPV4_ADDR_2);

        Assert.assertEquals(xorMappedAddressAttribute.getLength(), length);
        Assert.assertEquals(xorMappedAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(xorMappedAddressAttribute.getVariable(), newValue.array()));
    }

    @Test
    public void xorRelayAddressIPv6() throws UnknownHostException {
        short type = XOR_RELAY_ADDRESS.getType();
        short length = 4 + (128 / 8);
        ByteBuffer buf = constructBytesAddress((byte) 0x02, (short) 8080, IPV6_ADDR_1);

        Attribute attr = factory.get(type, length, buf.array());
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), buf.array()));

        XorRelayAddress xorRelayAddressAttribute = (XorRelayAddress) attr;
        xorRelayAddressAttribute.setAddress(xorRelayAddressAttribute.xorWithMagicCookie(IPV6_ADDR_2));
        xorRelayAddressAttribute.setPort(xorRelayAddressAttribute.xorWithMagicCookie((short) 8000));

        ByteBuffer newValue = constructBytesAddress((byte) 0x02, (short) 8000, IPV6_ADDR_2);

        Assert.assertEquals(xorRelayAddressAttribute.getLength(), length);
        Assert.assertEquals(xorRelayAddressAttribute.getType(), type);
        Assert.assertTrue(Arrays.equals(xorRelayAddressAttribute.getVariable(), newValue.array()));
    }

    @Test
    public void evenPort() {
        short type = EVEN_PORT.getType();
        short length = 4;
        byte[] data = new byte[4];
        data[0] = (byte) 0x00;

        Attribute attr = factory.get(type, length, data);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), data));

        EvenPort ep = (EvenPort) attr;
        Assert.assertFalse(ep.getReserveNextHigherPort());
        ep.setReserveNextHigherPort(true);
        data[0] = (byte) 0x80;
        Assert.assertEquals(ep.getLength(), length);
        Assert.assertEquals(ep.getType(), type);
        Assert.assertTrue(Arrays.equals(ep.getVariable(), data));
    }

    @Test
    public void requestedTransport() {
        // TODO: note to DPW want to check so we can enforce it
    }

    @Test
    public void dontFragment() {
        short type = DONT_FRAGMENT.getType();
        short length = 0;
        byte[] data = new byte[0];

        Attribute attr = new DontFragment(data);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), data));
    }

    @Test
    public void reservationToken() {
        short type = RESERVATION_TOKEN.getType();
        short length = 8;
        byte[] token = new byte[]{0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55};
        Attribute attr = factory.get(type, length, token);
        Assert.assertEquals(attr.getLength(), length);
        Assert.assertEquals(attr.getType(), type);
        Assert.assertTrue(Arrays.equals(attr.getVariable(), token));

        ReservationToken rt = (ReservationToken) attr;
        Assert.assertEquals(rt.getLength(), length);
        Assert.assertEquals(rt.getType(), type);
        Assert.assertTrue(Arrays.equals(rt.getVariable(), token));

        Assert.assertTrue(Arrays.equals(rt.getToken(), token));
    }

    private ByteBuffer constructBytesAddress(byte family, short port, byte[] address) {
        ByteBuffer buf = ByteBuffer.allocate(2 + 2 + address.length);
        buf.put(0, (byte) 0x00);
        buf.put(1, family);
        buf.putShort(2, port);
        for (int i = 0; i < address.length; i++) {
            buf.put(i + 4, address[i]);
        }
        return buf;
    }
}
