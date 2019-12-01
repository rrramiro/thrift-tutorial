package tutorial;/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

// Generated code


public class JavaServer {

  public static void main(String [] args) {
    try {
      CalculatorHandler handler = new CalculatorHandler();
      Calculator.Processor processor = new Calculator.Processor(handler);

      Runnable simple = new Runnable() {
        public void run() {
          simple(processor);
        }
      };      

      Runnable secure = new Runnable() {
        public void run() {
          secure(processor);
        }
      };

      Runnable nonBlocking = new Runnable() {
        public void run() {
          nonBlocking(processor);
        }
      };

      Runnable httpServer = new Runnable() {
        public void run() {
          httpServer(processor);
        }
      };

      new Thread(simple).start();
      new Thread(secure).start();
      new Thread(nonBlocking).start();
      new Thread(httpServer).start();
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

  public static void simple(Calculator.Processor processor) {
    try {
      TServerTransport serverTransport = new TServerSocket(9090);
      TServer server = new TSimpleServer(new TSimpleServer.Args(serverTransport).processor(processor));

      // Use this for a multithreaded server
      //TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

      System.out.println("Starting the simple server...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void secure(Calculator.Processor processor) {
    try {
      /*
       * Use TSSLTransportParameters to setup the required SSL parameters. In this example
       * we are setting the keystore and the keystore password. Other things like algorithms,
       * cipher suites, client auth etc can be set. 
       */
      TSSLTransportParameters params = new TSSLTransportParameters();
      // The Keystore contains the private key
      params.setKeyStore(".keystore", "thrift", null, null);

      /*
       * Use any of the TSSLTransportFactory to get a server transport with the appropriate
       * SSL configuration. You can use the default settings if properties are set in the command line.
       * Ex: -Djavax.net.ssl.keyStore=.keystore and -Djavax.net.ssl.keyStorePassword=thrift
       * 
       * Note: You need not explicitly call open(). The underlying server socket is bound on return
       * from the factory class. 
       */
      TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(9094, 0, null, params);
      TServer server = new TSimpleServer(new TSimpleServer.Args(serverTransport).processor(processor));

      // Use this for a multi threaded server
      // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

      System.out.println("Starting the secure server...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void nonBlocking(Calculator.Processor processor){
    try {
      TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(9091);
      TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));
      System.out.println("Starting the simple non blocking server...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void httpServer(Calculator.Processor processor) {
    Server server = null;
    try {
      server = new Server();
      ServerConnector connector = new ServerConnector(server);
      connector.setPort(9000);
      connector.setIdleTimeout(30000);
      server.addConnector(connector);
      ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
      TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
      TServlet tservlet = new TServlet(processor, protocolFactory, protocolFactory);
      servletContextHandler.addServlet(new ServletHolder(tservlet), "/calculator");
      server.setHandler(servletContextHandler);
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
