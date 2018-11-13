package cs107KNN;

import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

public class KMeansClustering {
	public static void main(String[] args) {
		int K = 1000;
		int maxIters = 20;

		byte[][][] images = KNN.parseIDXimages(Helpers.readBinaryFile("datasets/1000-per-digit_images_train"));
		byte[] labels = KNN.parseIDXlabels(Helpers.readBinaryFile("datasets/1000-per-digit_labels_train"));
		// 10 000 images et labels, pas 1000
		byte[][][] reducedImages = KMeansReduce(images, K, maxIters);

		byte[] reducedLabels = new byte[reducedImages.length];
		for (int i = 0; i < reducedLabels.length; i++) {
			reducedLabels[i] = KNN.knnClassify(reducedImages[i], images, labels, 7);
			System.out.println("Classified " + (i + 1) + " / " + reducedImages.length);
		}

		Helpers.writeBinaryFile("datasets/reduced10Kto1K_images", encodeIDXimages(reducedImages));
		Helpers.writeBinaryFile("datasets/reduced10Kto1K_labels", encodeIDXlabels(reducedLabels));
	}

	/*****************************************************************************/
	
	public static byte[] encodeIDXimages(byte[][][] images) {
		//on suppose que la donnees est correcte !!!!!!!!
		int nbImages = images.length;			//nb image
		int nbLignes = images[0].length;		//nb lignes/image
		int nbColonnes = images[0][0].length; 	//nb colonnes/image
		
		byte[] data = new byte[16 + nbImages*nbLignes*nbColonnes]; //taille de la donnee
		
		encodeInt(2051, data, 0);//nb magique des images 2051
		encodeInt(nbImages, data, 4);
		encodeInt(nbLignes, data, 8);
		encodeInt(nbColonnes, data, 12);//intitule du fichier IDX !
		
		for (int i = 0; i < nbImages; ++i) {
			for (int j = 0; j < nbLignes; ++j) {
				for (int k = 0; k < nbColonnes; ++k) {
					data[16 + i * nbLignes * nbColonnes + j * nbColonnes + k] = (byte) ((images[i][j][k] & 0xFF) + 128);
				} // on redecale tout de 128 car dans IDX on "lit" comme unsigned byte
			}
		}
		return data;
	}
	
	/*****************************************************************************/
   
	public static byte[] encodeIDXlabels(byte[] labels) {
		// on suppose que la donnees est correcte !!!!!!!!
		int nbEtiq = labels.length; // nb etiquettes

		byte[] data = new byte[8 + nbEtiq];// taille de la donnee

		encodeInt(2049, data, 0);// nb magique des etiquettes 2049
		encodeInt(nbEtiq, data, 4);

		for (int i = 0; i < nbEtiq; ++i) {
			data[8 + i] = labels[i];
		}
		return data;
	}

	/************************************************************************************/
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
		//on suppose que destination est initialisee !
	
		byte b1 = (byte) ((n >> 24) );		//on peux ne pas appliquer le masque pour b1
		byte b2 = (byte) ((n >> 16) & 0xFF);//car l'operateur >> met des zeros avant
		byte b3 = (byte) ((n >> 8 ) & 0xFF);
		byte b4 = (byte) ((n	  ) & 0xFF);// n = b1 | b2 | b3 | b4

		destination[offset + 0] = b1;
		destination[offset + 1] = b2;
		destination[offset + 2] = b3;
		destination[offset + 3] = b4;//assistante a dit qu'on ne decale pas
	}
	/************************************************************************************/
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
	
	/**********************************************************************************/
	
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
		// on suppose que les donnees sont ok
		for (int k = 0; k < tensor.length; ++k) {
			//et pas tensor[0].length car on veut le nb d'images !
			double distance = Double.MAX_VALUE;
			float newdistance = 0;
			for (int i = 0; i < centroids.length; ++i) {
				// et pas centroids[0].length car on veut le nb de centroid !
				newdistance = KNN.squaredEuclideanDistance(tensor[k], centroids[i]);
				if (newdistance <= distance) {
					// on peut mettre <= car peu probable qu'une image soit a egale distance de 2
					// centroids
					assignments[k] = i;
					distance = newdistance;
				}
			}
		}
	}

	/******************************************************************************/
    
	/**
     * @brief Computes the centroid of each cluster by averaging the images in the cluster
     * 
     * @param tensor the tensor of images to cluster
     * @param centroids the tensor of centroids that represent the cluster of images
     * @param assignments the vector indicating to what cluster each image belongs to.
     *  if j is at position i, then image i belongs to cluster j
     */
	public static void recomputeCentroids(byte[][][] tensor, byte[][][] centroids, int[] assignments) {
		// on suppose donees ok
		ArrayList<ArrayList<byte[][]>> clusters = new ArrayList<ArrayList<byte[][]>>();
		ArrayList<Integer> indiceClusters = new ArrayList<Integer>();
		// Pour simplifier, si un cluster reste vide, on ne modifiera pas son
		// representant. D'ou le choix d'un tableau dynamique.
		
		indiceClusters.add(assignments[0]);
		//indice du centroid le plus proche de la premiere image
		
		clusters.add(new ArrayList<byte[][]>());
		clusters.get(0).add(tensor[0]);
		// on met la premiere image dans le 1er cluster (celui dont le centroid est a la
		// position assignments[0] )

		//on initialise les tableaux pour que les iterations puissent commencer
		//encore une fois on ne se preoccupe pas de savoir si la donnees est correcte !!!

		organizeCluster(tensor, assignments, clusters, indiceClusters);

		for (int i = 0; i < indiceClusters.size(); ++i) {
			centroids[indiceClusters.get(i)] = newCentroid(clusters.get(i));
		}
	}
	/******************************************************************/
	//methode auxiliaire qui recalcule le centroid d'un cluster
	private static byte[][] newCentroid(ArrayList<byte[][]> cluster){
		//on suppose donnees ok
		int nbImages = cluster.size();
		int nbLines = cluster.get(0).length;
		int nbCols = cluster.get(0)[0].length;
		
		byte[][] newcenter = new byte[nbLines][nbCols];
		
		for(int i=0; i< nbLines; ++i) {
			for(int j=0; j< nbCols; ++j) {
				float moy = 0;
				for(int k=0; k<nbImages;++k) {
					moy += cluster.get(k)[i][j];
				}
				newcenter[i][j] = (byte) (moy/nbImages); 
			}
		}
		return newcenter;
	} 
	/********************************************************************/
	//methode auxiliaire qui classe chaque image dans son cluster
	private static void organizeCluster(byte[][][] tensor, int[] assignments, 
										ArrayList<ArrayList<byte[][]>> clusters, ArrayList<Integer> indiceClusters) {
		
		boolean ajoutee = false;

		for (int k = 1; k < assignments.length; k++) {
			// on commence a 1 car la premiere image est deja dans le premier cluster et
			// est associe au centroid a la position assignments[0]

			ajoutee = false;
						
			for (int j = 0; ajoutee == false && j < indiceClusters.size() ; j++) {

				/*
				 * le fait de demander si ajoutee est faux permet de reduire le temps
				 * d'execution
				 * 
				 * Si le tableau indiceClusters a deja la valeur de assignment[k] a sa position
				 * j, alors on ajoute l'image numero k au j-ieme cluster du tableau clusters
				 */

				if (assignments[k] == indiceClusters.get(j)) {
					// on verifie si le cluster associe a la k-ieme image exite deja dans notre
					// tableau de clusters
					clusters.get(j).add(tensor[k]);
					ajoutee = true;
				}
			}

				/*
				 * si le centroid associe a l'image k n'admet pas encore de cluster alors : 		
				 * 1 - on cre un nouveau cluster dans notre tableau clusters, et on ajoute l'image
				 * k a la premiere position de ce nouveau tableau
				 * 2 - on ajoute l'indice de ce "nouveau" centroid au tableau indiceClusters
				 */
			if (!ajoutee) {

				indiceClusters.add(assignments[k]);

				clusters.add(new ArrayList<byte[][]>());

				clusters.get(clusters.size() - 1).add(tensor[k]); // il faut ajouter un tableau d'image
				ajoutee = true;
			}

		}
	}
	/************************************************************************************/
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
