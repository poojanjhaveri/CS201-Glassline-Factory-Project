package shared;

public class Barcode {
	long barcode;
	public Barcode(long n){barcode = n;
	System.out.println("Barcode registered: " + Integer.toBinaryString((int)n));
	}
	
	public boolean [] translateToRecipe(){
			boolean[] recipe = new boolean[10];
			for (int i = 0; i < 10; i++){
				if ((barcode & (long)Math.pow(2, i)) == (long)Math.pow(2,i)){
					recipe[i] = true;
				}
				else 
					recipe[i] = false;
			}
			return recipe;
		}
	
	public static void main(String []args){
		long barcode = 0x2AA;
		Barcode bc = new Barcode(barcode);
		boolean [] recipe = bc.translateToRecipe();
		for (int i = 0; i < 10; i++)
			if (recipe[i])
			System.out.println("recipe " + (i+1));
		
	}
}

