package cs107KNN;

import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

public class KMeansClustering {
	public static void main(String[] args) {
		int K = 5000;
		int maxIters = 20;

		// TODO: Adaptez les parcours
		byte[][][] images = KNN.parseIDXimages(Helpers.readBinaryFile("TODO_remplacer/1000-per-digit_images_train"));
		byte[] labels = KNN.parseIDXlabels(Helpers.readBinaryFile("TODO_remplacer/1000-per-digit_labels_train"));

		byte[][][] reducedImages = KMeansReduce(images, K, maxIters);

		byte[] reducedLabels = new byte[reducedImages.length];
		for (int i = 0; i < reducedLabels.length; i++) {
			reducedLabels[i] = KNN.knnClassify(reducedImages[i], images, labels, 5);
			System.out.println("Classified " + (i + 1) + " / " + reducedImages.length);
		}

		Helpers.writeBinaryFile("datasets/reduced10Kto1K_images", encodeIDXimages(reducedImages));
		Helpers.writeBinaryFile("datasets/reduced10Kto1K_labels", encodeIDXlabels(reducedLabels));
	}

/*****************************************************************************/
	
	public static byte[] encodeIDXimages(byte[][][] images) {
		//on suppose que la données est correcte !!!!!!!!
		int nbImages = images.length;			//nb image
		int nbLignes = images[0].length;		//nb lignes/image
		int nbColonnes = images[0][0].length; 	//nb colonnes/image
		
		byte[] data = new byte[16 + nbImages*nbLignes*nbColonnes]; //taille de la donnée
		
		encodeInt(2051, data, 0);//nb magique des images 2051
		encodeInt(nbImages, data, 4);
		encodeInt(nbLignes, data, 8);
		encodeInt(nbColonnes, data, 12);//intitulé du fichier IDX !
		
		for (int i = 0; i < nbImages; ++i) {
			for (int j = 0; j < nbLignes; ++j) {
				for (int k = 0; k < nbColonnes; ++k) {
					data[16 + i * nbLignes * nbColonnes + j * nbColonnes + k] = (byte) ((images[i][j][k] & 0xFF) + 128);
				} // on redécale tout de 128 car dans IDX on "lit" comme unsigned byte
			}
		}
		return data;
	}
	
	/*****************************************************************************/
   
	public static byte[] encodeIDXlabels(byte[] labels) {
		// on suppose que la données est correcte !!!!!!!!
		int nbEtiq = labels.length; // nb étiquettes

		byte[] data = new byte[8 + nbEtiq];// taille de la donnée

		encodeInt(2049, data, 0);// nb magique des étiquettes 2049
		encodeInt(nbEtiq, data, 4);

		for (int i = 0; i < nbEtiq; ++i) {
			data[8 + i] = labels[i];
		}
		return data;
	}

    /**
     * @brief Decomposes an integer into 4 bytes stored consecutively in the destination
     * array starting at position offset
     * 
     * @param n the integer number to encode
     * @param destination the array where to write the encoded int
     * @param offset the position where to store the most significant byte of the integer,
     * the others will follow at offset + 1, offset + 2, offset + 3
     */
	public static void encodeInt(int n, byte[] destination, int offset) {
		//on suppose que destination est initialisé !
	
		byte b1 = (byte) ((n >> 24) & 0xFF);//on applique quand même le masque pour b1
		byte b2 = (byte) ((n >> 16) & 0xFF);//car si c'est negatif -> que des 1 avant
		byte b3 = (byte) ((n >> 8 ) & 0xFF);
		byte b4 = (byte) ((n	  ) & 0xFF);// n = b1 | b2 | b3 | b4

		destination[offset + 0] = b1;
		destination[offset + 1] = b2;
		destination[offset + 2] = b3;
		destination[offset + 3] = b4;//assistante a dit qu'on ne décale pas
	}

    /**
     * @brief Runs the KMeans algorithm on the provided tensor to return size elements.
     * 
     * @param tensor the tensor of images to reduce
     * @param size the number of images in the reduced dataset
     * @param maxIters the number of iterations of the KMeans algorithm to perform
     * 
     * @return the tensor containing the reduced dataset
     */
	public static byte[][][] KMeansReduce(byte[][][] tensor, int size, int maxIters) {
		int[] assignments = new Random().ints(tensor.length, 0, size).toArray();
		byte[][][] centroids = new byte[size][][];
		initialize(tensor, assignments, centroids);

		int nIter = 0;
		while (nIter < maxIters) {
			// Step 1: Assign points to closest centroid
			recomputeAssignments(tensor, centroids, assignments);
			System.out.println("Recomputed assignments");
			// Step 2: Recompute centroids as average of points
			recomputeCentroids(tensor, centroids, assignments);
			System.out.println("Recomputed centroids");

			System.out.println("KMeans completed iteration " + (nIter + 1) + " / " + maxIters);

			nIter++;
		}

		return centroids;
	}

   /**
     * @brief Assigns each image to the cluster whose centroid is the closest.
     * It modifies.
     * 
     * @param tensor the tensor of images to cluster
     * @param centroids the tensor of centroids that represent the cluster of images
     * @param assignments the vector indicating to what cluster each image belongs to.
     *  if j is at position i, then image i belongs to cluster j
     */
	public static void recomputeAssignments(byte[][][] tensor, byte[][][] centroids, int[] assignments) {
	}

    /**
     * @brief Computes the centroid of each cluster by averaging the images in the cluster
     * 
     * @param tensor the tensor of images to cluster
     * @param centroids the tensor of centroids that represent the cluster of images
     * @param assignments the vector indicating to what cluster each image belongs to.
     *  if j is at position i, then image i belongs to cluster j
     */
	public static void recomputeCentroids(byte[][][] tensor, byte[][][] centroids, int[] assignments) {
	}

    /**
     * Initializes the centroids and assignments for the algorithm.
     * The assignments are initialized randomly and the centroids
     * are initialized by randomly choosing images in the tensor.
     * 
     * @param tensor the tensor of images to cluster
     * @param assignments the vector indicating to what cluster each image belongs to.
     * @param centroids the tensor of centroids that represent the cluster of images
     *  if j is at position i, then image i belongs to cluster j
     */
	public static void initialize(byte[][][] tensor, int[] assignments, byte[][][] centroids) {
		Set<Integer> centroidIds = new HashSet<>();
		Random r = new Random("cs107-2018".hashCode());
		while (centroidIds.size() != centroids.length)
			centroidIds.add(r.nextInt(tensor.length));
		Integer[] cids = centroidIds.toArray(new Integer[] {});
		for (int i = 0; i < centroids.length; i++)
			centroids[i] = tensor[cids[i]];
		for (int i = 0; i < assignments.length; i++)
			assignments[i] = cids[r.nextInt(cids.length)];
	}
}
