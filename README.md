###Eris java client
Library to call Eris contracts from Java code. This library partially copy ___`eris/contracts-js`___ but not all features is implemented. 
#### Simple usage 
```$xslt
public class test {

    public static void main(String[] args) throws IOException {
        // Create contract manager with ABI file (contract specification)
        ContractManager contractManager = new ContractManager(new File("2DBE3BDA2E595FB86258E580A24D27415138C8AB"));

        // Create user whoo will call contract
        ErisAccountData account = new ErisAccountData() {
            @Override
            public String getAddress() {
                return "EB2970D7979D8F1B9F9CEAFD22CE311805502D7C";
            }
            @Override
            public String getPubKey() {
                return "E9507CB154695FB6D6EE05FA240B4BBFED397929757B80BA57B87003FD0EF660";
            }
            @Override
            public String getPrivKey() {
                return "CEA66CB7F7F5D7E642F50E5C0558D14E2666AAC19057826AC125119A7F3B1EFCE9507CB154695FB6D6EE05FA240B4BBFED397929757B80BA57B87003FD0EF660";
            }
        };

        String chainUrl = "http://172.17.0.1:1337";
        try (
                Create client (HTTP or WebSocet)
                RPCClient client = new HTTPRPCClient(chainUrl);
                //RPCClient client = new WebSocketRPCClient(chainUrl);

                Create contract
                Contract sj_testing_contract = contractManager.contractBuilder()
                        .withContractAddress("2DBE3BDA2E595FB86258E580A24D27415138C8AB")
                        .withRPCClient(client)
                        .withEventHandler(new EventHandler(chainUrl))
                        .withCallerAccount(account)
                        .build()
        ) {
             // Call contract method with required parameters
             return sj_testing_contract.call("queryBalance", "2DBE3BDA2E595FB86258E580A24D27415138C8AB");
        }

    }
}
```