/**
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 */
public class Main {
	public static void main(String[] args) {
		int id = 1;
		Station A = new Station("GareA", 3);
		A.setId(id++);
		Section Ab = new Section("Ab");
		Ab.setId(id++);
		Section bB = new Section("bB");
		bB.setId(id++);
		Station B = new Station("GareB", 3);
		B.setId(id++);
		Section BC = new Section("BC");
		BC.setId(id++);
		Station C = new Station("GareC", 3);
		C.setId(id++);
		Section CD = new Section("CD");
		CD.setId(id++);
		Station D = new Station("GareD", 3);
		D.setId(id++);
		Railway r = new Railway(new Element[] { A, Ab, bB, B,BC, C, CD, D });
		System.out.println("The railway is:");
		System.out.println("\t" + r);
		try {
			Train t1 = new Train("1", A, B, r);
			Train t2 = new Train("2", B, A, r);
//			Train t3 = new Train("3", A, C, r);
//			System.out.println(t1);
//			System.out.println(t2);
//			System.out.println(t3);
			new Thread(t1).start();
			new Thread(t2).start();
//			new Thread(t3).start();
		} catch (BadPositionForTrainException e) {
			System.out.println("Le train " + e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
