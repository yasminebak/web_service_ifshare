import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;




public class ApplicationShareProductMain {

	public static void main(String[] args) {
		
			String ip = "localhost";
			
			Registry r1 = LocateRegistry.getRegistry(ip, 1707);
			Registry r2 = LocateRegistry.getRegistry(ip, 1708);
			IManageEmployes employes = (IManageEmployes) r1.lookup("//" + ip + "/EmployeService");
			IIfShare ifshare = (IIfShare) r2.lookup("//" + ip + "/IfShareService");
		
			
			//Add products   //je sais pas pourquoi il prend pas de float
			ifshare.addProduct("L02456VR", "Livre", "La terre et le sang", 7);
			ifshare.addProduct("V01001TM", "Vêtement", "Pull", 10);
			ifshare.addProduct("R01002BE", "Vêtement", "Robe", 14); 
			ifshare.addProduct("P02001TL", "Vêtement", "Pantalon", 29);
			
			// Add Employe
			employes.addEmploye(01, "passwordYas", "Yasmine", "Bakour", "30/05/1996");
			employes.addEmploye(02, "passwordNas", "Nassima", "Ziani",  "06/12/1996");
			employes.addEmploye(03, "passwordAni", "Anissa",  "Cherfaoui", "20/03/1997");
			
			IApplicationShareProducts shareproduct = new ApplicationShareProduct(employes, ifshare);
			System.out.println("Start Application!");
				
			
		try {
		Registry r3 = LocateRegistry.createRegistry(1706);
		System.setProperty("java.rmi.server.hostname", ip);
		r3.rebind("//" + ip + "/BuyProductService", shareproduct);
		} catch (Exception e) {
			System.out.println("Trouble: " + e);	
	}
	}
}
