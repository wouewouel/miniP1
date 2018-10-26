package cs107KNN;

public class KNN {
	public static void main(String[] args) {


		// Charge les étiquettes depuis le disque 
		byte[] labelsRaw = Helpers.readBinaryFile("datasets/10-per-digit_labels_train"); 
		// Parse les étiquettes 
		byte[] labelsTrain = parseIDXlabels(labelsRaw); 
		// Affiche le nombre de labels 
		System.out.println(labelsTrain.length); 
		// Affiche le premier label 
		System.out.println(labelsTrain[0]);
		// Charge les images depuis le disque 
		byte[] imagesRaw = Helpers.readBinaryFile("datasets/10-per-digit_images_train"); 
		// Parse les images 
		byte[][][] imagesTrain = parseIDXimages(imagesRaw); 
		// Affiche les dimensions des images 
		System.out.println("Number of images : " + imagesTrain.length); 
		System.out.println("height : " + imagesTrain[0].length); 
		System.out.println("width : " + imagesTrain[0][0].length);
		// Affiche les 30 premières images et leurs étiquettes 
		Helpers.show("Test", imagesTrain, labelsTrain, 2, 15);

	}

	/**************************************************************************************/
	public static int extractInt(byte b31ToB24, byte b23ToB16, byte b15ToB8, byte b7ToB0) {	
		int a = b31ToB24<<24;			//on utilise pas de String car ça prend trop de place
		int b = b23ToB16<<16;			//l'opérateur <<n décale tout les 1 de ta bitestring vers la gauche
		int c = b15ToB8<<8;				//Il multiplie par 2^n en quelque sorte
		int d = b7ToB0;
		int nombreMagique = a | b | c | d;	//optimisation avec le bitwise "ou"
		return nombreMagique;
	}
	/**************************************************************************************/
	public static byte[][][] parseIDXimages(byte[] data) {
		if (data == null)		 { 	return null;	};
		if (data.length < 16)	 {	return null;	};							//on vérifie qu'on ne fera pas de 
		int nbmagique = extractInt(data [0],data [1],data [2],data [3]);		//segmentation fault
		if (nbmagique != 2051)	 {	return null;	}; 							//si nbmaqigue!=2051 ça renvoi null
		
		int nbImages = extractInt(data [4],data [5],data [6],data [7]);			//nb image
		int nbLignes = extractInt(data [8],data [9],data [10],data [11]);		//nb lignes/image
		int nbColonnes = extractInt(data [12],data [13],data [14],data [15]); 	//nb colonnes/image
		//pour cette vérif il y a un vrai problème que fait on ?
		assert (data.length == (16 + nbImages*nbLignes*nbColonnes) );			//on vérifie que la donnée est correcte
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
		if (data == null)		 { 	return null;	};							//on vérifie qu'on ne fera pas de 
		if (data.length < 8)	 {	return null;	};							//segmentation fault
		int nbmagique = extractInt(data [0],data [1],data [2],data [3]);		
		if (nbmagique != 2049)	 {	return null;	};							//si nbmaqigue!=2049 ça renvoi null
		
		int nbEtiq = extractInt(data [4],data [5],data [6],data [7]);			//nb étiquettes
		//pour cette vérif il y a un vrai problème que fait on ?
		assert (data.length == (8 + nbEtiq) );									//on vérifie que la donnée est correcte
		byte [] tab = new byte	[nbEtiq]; 
		for(int i=0; i < nbEtiq; ++i) {  
			tab[i] = data[8+i];
		}
		return tab; 
	}
/*****************************************************************************************/
	public static float squaredEuclideanDistance(byte[][] a, byte[][] b) {
	        //vérification des tableaux ? l'assistant a dit qu'on suppose qu'ils sont bon et vérifiera après
	        // Calcul de la distance euclidienne entre 2 images 
	        
	        float Distance = 0 ;
	        
	        for (int i =0 ; i< a.length ; i++) {
	            
	            for (int j =0 ; j < a[i].length ; j++) {
	                
	                Distance = (a[i][j] - b[i][j])*(a[i][j] - b[i][j]) + Distance ;	
	                //la somme entre byte est directement convertie en int donc pas de débordement #koul 
	            }    
	            
	        }
	        
	        
	        return Distance ;
	    }
/*****************************************************************************************/
    // Calcul de la moyenne des valeurs des pixels de 2 images
    
    public static float moyenne(byte[][] a) {
        float pixels = 0 ;
        
        // somme des valeurs des pixels
        
            for (int i =0 ; i < a.length ; i++) {
            
                for (int j =0 ; j < a[i].length ; j++) {
                
                    pixels = pixels + a[i][j] ;
                }
            
            }
        
        //division par le nombre de pixels
        
        float moy = pixels / (a.length * a[0].length);
        
        return moy ;
          
    }
/*****************************************************************************************/

    public static float invertedSimilarity(byte[][] a, byte[][] b) {
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
       
		//vérification des tableaux ? l'assistant a dit qu'on suppose qu'ils sont bon et vérifiera après
		int low = 0;
		int high = values.length -1;
		
		int[] indices = new int[values.length];
		for(int i = 0; i<values.length ; ++i) {
			indices[i]=i;									//initialisation du tableau {0,1,2....}
		}
	
		quicksortIndices(values, indices, low, high); 	//on fait appelle à l'autre fonction : c'est plus compacte	
		return indices;					
		//si le k-eme et le k+1-eme valeur sont les mêmes (peu probable) et que les images respéctives ont des labels
		//différents, que doit on faire ? avec le quick sort il prendra l'indices le plus proche du k-1-eme donc c'est 
		//plus du hasard qu'autre chose
	}

/***************************************************************************************************/
	
	public static void quicksortIndices(float[] values, int[] indices, int low, int high) {
        //vérification des tableaux ? l'assistant a dit qu'on suppose qu'ils sont bon et vérifiera après
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
        //vérification des tableaux ? l'assistant a dit qu'on suppose qu'ils sont bon et vérifiera après
		float a = values[i];
		values[i] = values[j];
		values[j] = a;
		
		int b = indices[i];
		indices[i] = indices[j];
		indices[j] = b;
	}
/*********************************************************************************************/
	public static int indexOfMax(int[] array) {
//		if(array == null) {		return 0; };			//pas bon !!!
		int index=0;
		int max	=array[0];
		for (int i = 0; i< array.length ; ++i ) {
			if( array[i]>max) {
				index = i;
			}
		}
		return index;
	}
/*********************************************************************************************/

	/**
	 * The k first elements of the provided array vote for a label
	 *
	 * @param sortedIndices the indices sorted by non-decreasing distance
	 * @param labels        the labels corresponding to the indices
	 * @param k             the number of labels asked to vote
	 *
	 * @return the winner of the election
	 */
	public static byte electLabel(int[] sortedIndices, byte[] labels, int k) {
		// TODO: ImplÃ©menter
		return 0;
	}

	/**
	 * Classifies the symbol drawn on the provided image
	 *
	 * @param image       the image to classify
	 * @param trainImages the tensor of training images
	 * @param trainLabels the list of labels corresponding to the training images
	 * @param k           the number of voters in the election process
	 *
	 * @return the label of the image
	 */
	public static byte knnClassify(byte[][] image, byte[][][] trainImages, byte[] trainLabels, int k) {
		// TODO: ImplÃ©menter
		return 0;
	}

	/**
	 * Computes accuracy between two arrays of predictions
	 * 
	 * @param predictedLabels the array of labels predicted by the algorithm
	 * @param trueLabels      the array of true labels
	 * 
	 * @return the accuracy of the predictions. Its value is in [0, 1]
	 */
	public static double accuracy(byte[] predictedLabels, byte[] trueLabels) {
		// TODO: ImplÃ©menter
		return 0d;
	}
}
