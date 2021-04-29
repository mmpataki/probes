


public class DomainUtil {
    
    ClientFactory clientFactory;

    private DomainService createDomainService(ProcessInfo processInfo)
        throws ServicesFrameworkException, ComponentException {
      ConnectionParamsBuilder<DomainService> paramBuilder = ConnectionParams
          .builder(processInfo.getIspNodeAddress().getAddress().getHost(),
              processInfo.getIspNodeAddress().getAddress().getPort(), DomainService.DOMAIN_SERVICE_NAME,
              DomainService.class)
          .jsfPort(true).secureCommState(isTlsEnabled);
    
      return getClientFactory().getService(paramBuilder.build());
    }

    private synchronized ClientFactory getClientFactory() throws ComponentException {
      if (clientFactory != null) {
        return clientFactory;
      }
      clientFactory = (ClientFactory) ComponentFramework.getInstance().getComponent(CLIENT_FACTORY);
    }

    public static void main(String[] args) {
        new DomainUtil().start();
    }

}
