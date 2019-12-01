package tutorial;

import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.transport.*;
import shared.*;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class AsyncJavaClient {
    public static void main(String[] args) {
        try {
            TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
            TAsyncClientManager clientManager = new TAsyncClientManager();
            Calculator.AsyncClient.Factory clientFactory = new Calculator.AsyncClient.Factory(clientManager, protocolFactory);

            perform(clientFactory);
        } catch (TException | IOException | InterruptedException x) {
            x.printStackTrace();
        }
    }

    private static void perform(Calculator.AsyncClient.Factory clientFactory) throws TException, IOException, InterruptedException {
        Semaphore sem = new Semaphore(1);
        TNonblockingTransport transport = new TNonblockingSocket("localhost", 9091);
        Calculator.AsyncClient client = clientFactory.getAsyncClient(transport);
        sem.acquire();
        client.ping(new AsyncMethodCallback<Void>() {
            @Override
            public void onComplete(Void response) {
                System.out.println("ping()");
                sem.release();
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
                sem.release();
            }
        });

        sem.acquire();
        client.add(1, 1, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer sum) {
                System.out.println("1+1=" + sum);
                sem.release();
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
                sem.release();
            }
        });


        Work work = new Work();
        work.op = Operation.DIVIDE;
        work.num1 = 1;
        work.num2 = 0;

        sem.acquire();
        client.calculate(1, work, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer quotient) {
                System.out.println("Whoa we can divide by 0");
                sem.release();
            }

            @Override
            public void onError(Exception exception) {
                System.out.println("Invalid operation: " + exception);
                sem.release();
            }
        });


        sem.acquire();
        if (client.hasError()) {
            transport = new TNonblockingSocket("localhost", 9091);
            client = clientFactory.getAsyncClient(transport);
        }

        work.op = Operation.SUBTRACT;
        work.num1 = 15;
        work.num2 = 10;

        client.calculate(1, work, new AsyncMethodCallback<Integer>() {
            @Override
            public void onComplete(Integer diff) {
                System.out.println("15-10=" + diff);
                sem.release();
            }

            @Override
            public void onError(Exception exception) {
                System.out.println("Invalid operation: " + exception);
                sem.release();
            }
        });

        sem.acquire();
        client.getStruct(1, new AsyncMethodCallback<SharedStruct>() {
            @Override
            public void onComplete(SharedStruct log) {
                System.out.println("Check log: " + log.value);
                sem.release();
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
                sem.release();
            }
        });
        sem.acquire();
    }
}
