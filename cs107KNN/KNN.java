package cs107KNN;

import java.util.Arrays;

public class KNN {
	public static void main(String[] args) {
		//Test 2: byte array with 2 images of size 2x2

		byte[] bArray2 = new byte[] {0,0,8,3, 0,0,0,2, 0,0,0,2, 0,0,0,2, 1,1,1,1, 0,0,0,0};
		byte[][][] tensor2 = parseIDXimages(bArray2);

		byte[][][] expectedTensor2 = new byte[][][] {
		            { {-127,-127}, {-127,-127} }, //image 0
		            { {-128,-128}, {-128,-128} }  //image 1            
		};
		System.out.println(Arrays.deepEquals(tensor2, expectedTensor2));	
	}

	/**************************************************************************************/
	
	public static int extractInt(byte b31ToB24, byte b23ToB16, byte b15ToB8, byte b7ToB0) {	
		int a = (b31ToB24& 0xFF)<<24 	;			//on utilise pas de String car �a prend trop de place
		int b = (b23ToB16& 0xFF)<<16 	;			//l'op�rateur <<n d�cale tout les 1 de ta bitestring vers la gauche
		int c = (b15ToB8& 0xFF)<<8 		;			//Il multiplie par 2^n en quelque sorte
		int d = (b7ToB0	& 0xFF)			;

		return (a | b | c | d);						//optimisation avec le bitwise "ou"
	}
	
	/**************************************************************************************/
	
	public static byte[][][] parseIDXimages(byte[] data) {
		if (data == null)		 { 	return null;	};
		if (data.length < 16)	 {	return null;	};							//on v�rifie qu'on ne fera pas de 
		int nbmagique = extractInt(data [0],data [1],data [2],data [3]);		//segmentation fault
		if (nbmagique != 2051)	 {	return null;	}; 							//si nbmaqigue!=2051 �a renvoi null
		
		int nbImages = extractInt(data [4],data [5],data [6],data [7]);			//nb image
		int nbLignes = extractInt(data [8],data [9],data [10],data [11]);		//nb lignes/image
		int nbColonnes = extractInt(data [12],data [13],data [14],data [15]); 	//nb colonnes/image
		//pour cette v�rif il y a un vrai probl�me que fait on ?
		assert (data.length == (16 + nbImages*nbLignes*nbColonnes) );			//on v�rifie que la donn�e est correcte
		byte [][][] tenseur = new byte	[nbImages][nbLignes][nbColonnes];
		for(int i= 0; i < nbImages; ++i ) {
			for(int j=0; j< nbLignes; ++j ) {
				for(int k=0; k < nbColonnes; ++k) {
					tenseur[i][j][k] = (byte) ((data[16+ i*nbLignes*nbColonnes + j*nbColonnes +k] & 0xFF) - 128) ;
				}
			}
		}

		return tenseur;
	}

	/**************************************************************************************/

	public static byte[] parseIDXlabels(byte[] data) {
		if (data == null)		 { 	return null;	};							//on v�rifie qu'on ne fera pas de 
		if (data.length < 8)	 {	return null;	};							//segmentation fault
		int nbmagique = extractInt(data [0],data [1],data [2],data [3]);		
		if (nbmagique != 2049)	 {	return null;	};							//si nbmaqigue!=2049 �a renvoi null
		
		int nbEtiq = extractInt(data [4],data [5],data [6],data [7]);			//nb �tiquettes
		//pour cette v�rif il y a un vrai probl�me que fait on ?
		assert (data.length == (8 + nbEtiq) );									//on v�rifie que la donn�e est correcte
		byte[] tab = new byte[nbEtiq];
		for (int i = 0; i < nbEtiq; ++i) {
			tab[i] = data[8 + i];
		}
		return tab;
	}

	/*****************************************************************************************/
	
	public static float squaredEuclideanDistance(byte[][] a, byte[][] b) {
		//pas de v�rification des tableaux selon l'assistant principal
	        float Distance = 0 ;
	     // Calcul de la distance euclidienne entre 2 images     
	        for (int i =0 ; i< a.length ; i++) {
	            
	            for (int j =0 ; j < a[i].length ; j++) {		//pourquoi a[i] et pas b[i] ?
	                
	                Distance = (a[i][j] - b[i][j])*(a[i][j] - b[i][j]) + Distance ;	
	                //la somme entre byte est directement convertie en int donc pas de d�bordement #koul 
	            }    
	            
	        }
	        
	        
	        return Distance ;
	    }

	/*****************************************************************************************/
    
	public static float moyenne(byte[][] a) {			// Calcul de la moyenne des valeurs des pixels de 2 images
        float pixels = 0 ;
        
        // somme des valeurs des pixels

		for (int i = 0; i < a.length; i++) {

			for (int j = 0; j < a[i].length; j++) {

				pixels = pixels + a[i][j];
			}

		}
        //division par le nombre de pixels -> pas besoin de cr�er une variable float
        return (pixels / (a.length * a[0].length));	
          
    }

	/*****************************************************************************************/

    public static float invertedSimilarity(byte[][] a, byte[][] b) {
		//pas de v�rification des tableaux selon l'assistant principal
        float A = moyenne(a);
        float B = moyenne(b);
        float denompart1 = 0 ;
        float denompart2 =0 ;
        
        // calcul des 2 facteurs du denominateur
        for(int i =0 ; i < a.length ; ++i) {
            
            for(int j=0 ; j < a[i].length ; j++) {
                
                denompart1 = denompart1 + (a[i][j] - A)*(a[i][j] - A);
                denompart2 = denompart2 + (b[i][j] - B)*(b[i][j] - B);
            }
            
        }
        
        // calcul du denominateur
        float denominateur = (float) Math.sqrt(denompart1 * denompart2);
        
            if (denominateur ==0) {
                return 2;
            }
        
        float numerateur = 0;
        
        // calcul du numerateur
        
        for (int i =0 ; i < a.length ; i++) {
            
            for (int j =0 ; j< a[i].length ; j++) {
                
                numerateur = numerateur + (a[i][j] - A)*(b[i][j] - B) ;
                
            }
            
        }
         
        // calcul total
        
        float SI = 1 - (numerateur / denominateur) ;
        
        return SI;												//optimisation
    }


    /********************************************************************************************/
	
    public static int[] quicksortIndices(float[] values) {
		//pas de v�rification des tableaux selon l'assistant principal
		int low = 0;
		int high = values.length -1;
		
		int[] indices = new int[values.length];
		for(int i = 0; i<values.length ; ++i) {
			indices[i]=i;						//initialisation du tableau {0,1,2....}
		}
	
		quicksortIndices(values, indices, low, high); 	//on fait appelle � l'autre fonction : c'est plus compacte	
		return indices;					
		//si le k-eme et le k+1-eme valeur sont les m�mes (peu probable) et que les images resp�ctives ont des labels
		//diff�rents, que doit on faire ? avec le quick sort il prendra l'indices le plus proche du k-1-eme donc c'est 
		//plus du hasard qu'autre chose
	}


    /***************************************************************************************************/
	
	public static void quicksortIndices(float[] values, int[] indices, int low, int high) {
		//pas de v�rification des tableaux selon l'assistant principal
		int l = low;								
		int h = high;
		float pivot = values[low];
		
		while(l<=h) {
			if(values[l] <pivot) {					//on veut comparer des float 
				++l;
			}else {
				if(values[h]>pivot) {
					--h;
				}else {
					swap(l,h,values,indices);
					++l;
					--h; 
					}
			}
		}
		
		if(low < h) {
			quicksortIndices(values, indices, low, h);
		}
		if(high > l) {
			quicksortIndices(values, indices, l, high);
		}
	}


	/*********************************************************************************************/
	
	public static void swap(int i, int j, float[] values, int[] indices) {
		//pas de v�rification des tableaux selon l'assistant principal
		float a = values[i];
		values[i] = values[j];
		values[j] = a;
		
		int b = indices[i];
		indices[i] = indices[j];
		indices[j] = b;
	}

	/*********************************************************************************************/
	
	public static int indexOfMax(int[] array) {
		//pas de v�rification des tableaux selon l'assistant principal
		int index=0;
		int max	=array[0];
		for (int i = 0; i< array.length ; ++i ) {
			if( array[i]>max) {							// pas >= car on prend la premi�re dans l'ordre
				index = i;
				max = array[i];
			}
		}
		return index;
	}

	/*********************************************************************************************/

	public static byte electLabel(int[] sortedIndices, byte[] labels, int k) {
		int[] kvotes = new int[10];						//c'est un tableau d'int car peut �tre qu'il y aura beaucoup
		for(int i=0; i<k; ++i) {						//de votes : mais � v�rifier car byte est plus sympa
			
			++ kvotes[labels[sortedIndices[i]]] ;		//on veut ce qu'il y a sur les k 1ere �tiquettes tri�es
		
		}
		return (byte) indexOfMax(kvotes);				//on veut l'�tiquette du + grand index
		//je pense qu'on peut faire mieux que �a !
	}

	/*********************************************************************************************/

	public static byte knnClassify(byte[][] image, byte[][][] trainImages, byte[] trainLabels, int k) {
		//pas de v�rification des tableaux selon l'assistant principal
		float[] values = new float[trainImages.length];
		
		for(int i=0; i< trainImages.length; ++i) {
			values[i] = invertedSimilarity(image, trainImages[i]);		//nous cr�ons le tableau des distances
		}

		return electLabel(quicksortIndices(values), trainLabels, k);
		// public static float squaredEuclideanDistance(byte[][] a, byte[][] b) {
		// public static float invertedSimilarity(byte[][] a, byte[][] b) {

	}

	/*********************************************************************************************/

	public static double accuracy(byte[] predictedLabels, byte[] trueLabels) {

		double n = predictedLabels.length;
		double accuracy = 0.;

		for (int i = 0; i < n; i++) {
			if (predictedLabels[i] == trueLabels[i]) {
				accuracy = accuracy + 1;
			}

		}
		accuracy = accuracy / n;

		return accuracy;
	}
}


