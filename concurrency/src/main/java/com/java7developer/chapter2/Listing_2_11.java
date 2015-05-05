package com.java7developer.chapter2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;

import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

public class Listing_2_11 {

  public static void main(String[] args) {
    try {
      NetworkInterface networkInterface = NetworkInterface.getByName("net1");

      DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET);

      dc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
      dc.bind(new InetSocketAddress(8080));
      dc.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);

      InetAddress group = InetAddress.getByName("180.90.4.12");
      MembershipKey key = dc.join(group, networkInterface);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
