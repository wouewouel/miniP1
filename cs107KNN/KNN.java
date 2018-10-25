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
		int nombreMagique = a + b + c + d;
		return nombreMagique;
	}
	/**************************************************************************************/
	public static byte[][][] parseIDXimages(byte[] data) {
		//Regarder si on ne met pas null à la place	!!!!
		assert data != null;
		assert (data.length >= 16);												//on vérifie qu'on ne fera pas de 
		int nbmagique = extractInt(data [0],data [1],data [2],data [3]);		//segmentation fault
		assert (nbmagique == 2051); 											//si nbmaqigue!=2051 ça envoi une erreur
		
		int nbImages = extractInt(data [4],data [5],data [6],data [7]);			//nb image
		int nbLignes = extractInt(data [8],data [9],data [10],data [11]);		//nb lignes/image
		int nbColonnes = extractInt(data [12],data [13],data [14],data [15]); 	//nb colonnes/image
		
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
		assert data != null;
		assert (data.length >= 8);												//on vérifie qu'on ne fera pas de 
		int nbmagique = extractInt(data [0],data [1],data [2],data [3]);		//segmentation fault
		assert (nbmagique == 2049); 											//si nbmaqigue!=2049 ça envoi une erreur
		
		int nbEtiq = extractInt(data [4],data [5],data [6],data [7]);			//nb étiquettes
		assert (data.length == (8 + nbEtiq) );									//on vérifie que la donnée est correcte
		byte [] tab = new byte	[nbEtiq];
		for(int i=0; i < nbEtiq; ++i) {
			tab[i] = data[8+i];
		}
		return tab;
	}
/*****************************************************************************************/
	public static float squaredEuclideanDistance(byte[][] a, byte[][] b) {
	        
	        // Calcul de la distance euclidienne entre 2 images 
	        
	        float Distance = 0 ;
	        
	        for (int i =0 ; i< a.length ; i++) {
	            
	            for (int j =0 ; j < a[i].length ; j++) {
	                
	                Distance = (a[i][j] - b[i][j])*(a[i][j] - b[i][j]) + Distance ;	//Est-ce qu'on est sur que ça ne 
	                																//déborde pas ?
	            }    
	            
	        }
	        
	        
	        return Distance ;
	    }
/*****************************************************************************************/
	/**
	 * @brief Computes the inverted similarity between 2 images.
	 * 
	 * @param a, b two images of same dimensions
	 * 
	 * @return the inverted similarity between the two images
	 */
	public static float invertedSimilarity(byte[][] a, byte[][] b) {
		// TODO: ImplÃ©menter
		return 0f;
	}

	/**
	 * @brief Quicksorts and returns the new indices of each value.
	 * 
	 * @param values the values whose indices have to be sorted in non decreasing
	 *               order
	 * 
	 * @return the array of sorted indices
	 * 
	 *         Example: values = quicksortIndices([3, 7, 0, 9]) gives [2, 0, 1, 3]
	 */
	public static int[] quicksortIndices(float[] values) {
		// TODO: ImplÃ©menter
		return null;
	}

	/**
	 * @brief Sorts the provided values between two indices while applying the same
	 *        transformations to the array of indices
	 * 
	 * @param values  the values to sort
	 * @param indices the indices to sort according to the corresponding values
	 * @param         low, high are the **inclusive** bounds of the portion of array
	 *                to sort
	 */
	public static void quicksortIndices(float[] values, int[] indices, int low, int high) {
		// TODO: ImplÃ©menter
	}

	/**
	 * @brief Swaps the elements of the given arrays at the provided positions
	 * 
	 * @param         i, j the indices of the elements to swap
	 * @param values  the array floats whose values are to be swapped
	 * @param indices the array of ints whose values are to be swapped
	 */
	public static void swap(int i, int j, float[] values, int[] indices) {
		// TODO: ImplÃ©menter
	}

	/**
	 * @brief Returns the index of the largest element in the array
	 * 
	 * @param array an array of integers
	 * 
	 * @return the index of the largest integer
	 */
	public static int indexOfMax(int[] array) {
		// TODO: ImplÃ©menter
		return 0;
	}

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
