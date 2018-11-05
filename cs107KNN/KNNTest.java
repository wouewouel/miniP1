package cs107KNN;

import java.util.Arrays;

public class KNNTest {
	public static void main(String[] args) {
		// extractIntTest();
		// parsingTest();
		// squaredEuclideanDistanceTest();
		// invertedSimilarityTest();
		// quicksortTest();
		// indexOfMaxTest();
		// electLabelTest();
		// knnClassifyTest();
		//accuracyTest();
		finalTest();
	}

	public static void extractIntTest() {
		byte b1 = 40; // 00101000
		byte b2 = 20; // 00010100
		byte b3 = 10; // 00001010
		byte b4 = 5; // 00000101

		// [00101000 | 00010100 | 00001010 | 00000101] = 672401925

		String expected = Helpers.byteToBinaryString(b1) + Helpers.byteToBinaryString(b2)
				+ Helpers.byteToBinaryString(b3) + Helpers.byteToBinaryString(b4);

		int obtained = KNN.extractInt(b1, b2, b3, b4);

		System.out.println("=== Test extractInt ===");
		System.out.println("Entier attendu:\t \t " + Integer.parseInt(expected, 2));
		System.out.println("extractInt produit:\t " + obtained);// "00101000000101000000101000000101"

	}

	public static void parsingTest() {
		System.out.println("=== Test parsing ===");
		byte[][][] images = KNN.parseIDXimages(Helpers.readBinaryFile("datasets/10-per-digit_images_train"));
		byte[] labels = KNN.parseIDXlabels(Helpers.readBinaryFile("datasets/10-per-digit_labels_train"));

		System.out.println("Number of images: " + images.length);
		System.out.println("Height: " + images[0].length);
		System.out.println("Width: " + images[0][0].length);

		Helpers.show("Test parsing", images, labels, 10, 10);
	}

	public static void squaredEuclideanDistanceTest() {
		System.out.println("=== Test distance euclidienne ===");
		byte[][] a = new byte[][] { { 1, 1 }, { 2, 2 } };
		byte[][] b = new byte[][] { { 3, 3 }, { 4, 4 } };

		System.out.println("Distance calculÃ©e: " + KNN.squaredEuclideanDistance(a, b));
		System.out.println("Distance attendue: 16.0");
	}

	public static void invertedSimilarityTest() {
		System.out.println("=== Test similarité inversée ===");
		byte[][] a = new byte[][] { { 1, 1 }, { 1, 2 } };
		byte[][] b = new byte[][] { { 50, 50 }, { 50, 100 } };

		System.out.println("Distance calculée: " + KNN.invertedSimilarity(a, b));
		System.out.println("Distance attendue: 0.0");
	}

	public static void quicksortTest() {
		System.out.println("=== Test quicksort ===");
		float[] data = new float[] { 3, 1, -1, 0, 9 };
		int[] result = KNN.quicksortIndices(data);

		System.out.println("Indices triés: " + Arrays.toString(result));
	}

	public static void indexOfMaxTest() {
		System.out.println("=== Test indexOfMax ===");
		int[] data = new int[] { 10, 5, 10, 1 };

		int indexOfMax = KNN.indexOfMax(data);
		System.out.println("Indices: [0, 1, 2, 3]");
		System.out.println("Données: " + Arrays.toString(data));
		System.out.println("L'indice de l'élément maximal est: " + indexOfMax);
	}

	public static void electLabelTest() {
		System.out.println("=== Test electLabel ===");
		int[] sortedIndices = new int[] { 0, 3, 2, 1 };
		byte[] labels = new byte[] { 2, 1, 1, 2 };
		int k = 3;

		System.out.println("Etiquette votée: \t " + KNN.electLabel(sortedIndices, labels, k));
		System.out.println("Etiquette attendue: \t 2");
	}

	public static void knnClassifyTest() {
		System.out.println("=== Test predictions ===");
		byte[][][] imagesTrain = KNN.parseIDXimages(Helpers.readBinaryFile("datasets/10-per-digit_images_train"));
		byte[] labelsTrain = KNN.parseIDXlabels(Helpers.readBinaryFile("datasets/10-per-digit_labels_train"));

		byte[][][] imagesTest = KNN.parseIDXimages(Helpers.readBinaryFile("datasets/10k_images_test"));
		byte[] labelsTest = KNN.parseIDXlabels(Helpers.readBinaryFile("datasets/10k_labels_test"));

		byte[] predictions = new byte[200];
		for (int i = 0; i < 200; i++) {
			predictions[i] = KNN.knnClassify(imagesTest[i], imagesTrain, labelsTrain, 7);
		}
		Helpers.show("Test predictions", imagesTest, predictions, labelsTest, 10, 20);
	}

	public static void accuracyTest() {
		System.out.println("=== Test précision ===");
		byte[] a = new byte[] { 1, 1, 1, 1 };
		byte[] b = new byte[] { 1, 1, 1, 9 };

		System.out.println("Précision calculée: " + KNN.accuracy(a, b));
		System.out.println("Précision attendue:  0.75");
	}

	public static void finalTest() {
		System.out.println("=== Test final ===");
		byte[][][] imagesTrain = KNN.parseIDXimages(Helpers.readBinaryFile("datasets/1000-per-digit_images_train"));
		byte[] labelsTrain = KNN.parseIDXlabels(Helpers.readBinaryFile("datasets/1000-per-digit_labels_train"));

		byte[][][] imagesTest = KNN.parseIDXimages(Helpers.readBinaryFile("datasets/10k_images_test"));
		byte[] labelsTest = KNN.parseIDXlabels(Helpers.readBinaryFile("datasets/10k_labels_test"));

		int TESTS = 1000;
		int K = 7;

		byte[] predictions = new byte[TESTS];
		long start = System.currentTimeMillis();
		for (int i = 0; i < TESTS; i++) {
			predictions[i] = KNN.knnClassify(imagesTest[i], imagesTrain, labelsTrain, K);
		}

		long end = System.currentTimeMillis();
		double time = (end - start) / 1000d;
		System.out.println("Accuracy = " + KNN.accuracy(predictions, Arrays.copyOfRange(labelsTest, 0, TESTS)) + " %");
		System.out.println("Time = " + time + " seconds");
		System.out.println("Time per test image = " + (time / TESTS));

	}
}
