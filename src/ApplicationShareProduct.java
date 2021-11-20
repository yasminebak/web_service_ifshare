import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ApplicationShareProduct extends UnicastRemoteObject implements IApplicationShareProducts {

	/**
	 * 
	 */
	private IManageEmployes employes;
	private IIfShare ifshare;
	private HashMap<IClient, List<IProduct>> historyWishList;
	private HashMap<IProduct, IClient> state;
	private ArrayList<IClient> logClient;
	private HashMap<IProduct, LinkedList<IClient>> fifo;
	private static final long serialVersionUID = 1L;

	public ApplicationShareProduct(IManageEmployes employes, IIfShare ifshare) throws RemoteException {
		super();
		this.employes = employes;
		this.ifshare = ifshare;
	}

	@Override
	public String subscribe(IClient client) throws RemoteException {

		int id = client.getClientId();
		String password = client.getClientPassword();

		if (!employes.getEmploye(id).verifIdentity(password)) {
			return "User or Password not correct !";
		}
		logClient.add(client);

		return "You are connected";
	}

	@Override
	public String getAllProducts() throws RemoteException {
		List<IProduct> products = ifshare.getAllProduct();
		StringBuilder sb = new StringBuilder("");
		for (IProduct product : products) {
			sb.append(product.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public String getAvailableProducts() throws RemoteException {
		List<IProduct> products = ifshare.getAvailableProduct();
		StringBuilder sb = new StringBuilder("");
		for (IProduct product : products) {
			sb.append(product.toString());
			sb.append("\n");		
		}
		return sb.toString();
	}

	@Override
	public String getTypeProducts(String type) throws RemoteException {
		List<IProduct> products = ifshare.showTypeProduct(type);
		StringBuilder sb = new StringBuilder("");
		for (IProduct product : products) {
			sb.append(product.toString());
			sb.append("\n");		
		}
		return sb.toString();
	}

	@Override
	public String buyProduct(String id, IClient client) throws RemoteException {
		if (id == null || client == null) {
			return "error";
		}
		if (!logClient.contains(client)) {
			return "You are not logged, subscribe or create a count";
		}

		IProduct product;
		try {
			product = ifshare.getProduct(id);
			if (product.isAvailable()) {
				product.setAvailable(false);
				// exprimer le fifo en supprimant de la liste le client qui a acheté le produit
				// souhaité
				for (Entry<IProduct, LinkedList<IClient>> e : fifo.entrySet()) {
					product.setAvailable(false);
					LinkedList<IClient> listClient = e.getValue();
					if (listClient.contains(client)) {
						listClient.remove(client);
					}

				}
				// associer le produit au client et le mettre dans une hashmap pour voir quel client veut quel produit
				state.put(product, client);
				return "you can buy your product";
			}
		} catch (Exception e) {
			return "Unknown id : " + id;
		}
		if (fifo.containsKey(product)) {
			if (!fifo.get(product).contains(client)) {
				fifo.get(product).add(client);
				return "Sorry, this product is not available, you will be notified when it will be available again!";
			}else {
				return "You have an order in progress !";
			}
		} else {
			LinkedList<IClient> clientList = new LinkedList<>();
			clientList.add(client);
			fifo.put(product, clientList);
			return "Sorry, this product is not available, you will be notified when it will be available again!";
		}

	}
	
	public void updateFifo(IProduct product) throws RemoteException {
		
		if (!fifo.get(product).isEmpty()) {
			IClient client = fifo.get(product).pollFirst();
			state.put(product, client);

			if (historyWishList.containsKey(client)) {
				historyWishList.get(client).add(product);
			} else {
				ArrayList<IProduct> historyList = new ArrayList<>();
				historyList.add(product);
				historyWishList.put(client, historyList);
			}
			client.notifyAvailableProduct("Your product is available again!");
		}
	}

}
