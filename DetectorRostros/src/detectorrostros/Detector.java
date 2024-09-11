package detectorrostros;

import java.awt.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author JOSEA
 */
public class Detector {

    private JFileChooser fileChooser;
    private ImageIcon imageIcon;
    private File selectedFile;
    private String rutaImagen;
    private int rostros;

    private CascadeClassifier faceCascade;
    private Mat image;

    public Detector() {
        //Carga la biblioteca nativa de OpenCV en el programa Java.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Devuelve la cantidad de rostros que se encontro en la imagen
     *
     * @return
     */
    public int getRostros() {
        return rostros;
    }

    public void detectarRostros(String ruta, JLabel label) {
        this.image = Imgcodecs.imread(ruta);

        this.faceCascade = new CascadeClassifier("data/haarcascade_eye.xml");
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(image, faces);
        
        
        this.rostros = faces.toArray().length;

        for (Rect rect : faces.toArray()) {
//            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0, 255, 0) //aqui se le cambia el color en R,G,B -> 0, 255, 0
//                    , 2);
            Point center = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
            Imgproc.circle(image
                    , center
                    , Math.max(rect.width, rect.height) / 2 //radius
                    , new Scalar(0, 0, 255) // Color verde
                    , 4 //thickness
            );
        }

        mostrarImagen(Mat2BufferedImage(image), label);

    }

    /**
     * Convierte una imagen en formato Mat de OpenCV a un objeto BufferedImage
     * de Java.
     *
     * @param image La imagen en formato Mat que se desea convertir.
     * @return El objeto BufferedImage resultante.
     */
//    private BufferedImage Mat2BufferedImage(Mat image) {
//        // Determina el tipo de BufferedImage basado en el número de canales de la imagen
//        int bufferedImageType = image.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
//
//        // Crea un nuevo objeto BufferedImage con el tamaño de la imagen
//        BufferedImage bufferedImage = new BufferedImage(image.cols(), image.rows(), bufferedImageType);
//
//        // Obtiene los datos de píxeles de la imagen de formato Mat
//        byte[] data = new byte[image.cols() * image.rows() * (int) image.elemSize()];
//        image.get(0, 0, data);
//
//        // Establece los datos de píxeles en el objeto BufferedImage
//        bufferedImage.getRaster().setDataElements(0, 0, image.cols(), image.rows(), data);
//
//        // Retorna el objeto BufferedImage resultante
//        return bufferedImage;
//    }
    private BufferedImage Mat2BufferedImage(Mat image) {
        int bufferedImageType = BufferedImage.TYPE_INT_RGB; // Cambiamos el tipo de BufferedImage a RGB

        BufferedImage bufferedImage = new BufferedImage(image.cols(), image.rows(), bufferedImageType);

        byte[] data = new byte[image.cols() * image.rows() * (int) image.elemSize()];
        image.get(0, 0, data);

        // Convierte los datos de píxeles a RGB
        int[] rgbData = new int[image.cols() * image.rows()];
        for (int i = 0; i < data.length; i += 3) {
            int blue = data[i] & 0xFF;
            int green = data[i + 1] & 0xFF;
            int red = data[i + 2] & 0xFF;

            rgbData[i / 3] = (red << 16) | (green << 8) | blue;
        }

        // Establece los datos de píxeles en el objeto BufferedImage
        bufferedImage.setRGB(0, 0, image.cols(), image.rows(), rgbData, 0, image.cols());

        return bufferedImage;
    }

    /**
     * Permite al usuario seleccionar un archivo de imagen utilizando
     * JFileChooser.
     *
     * @return La ruta absoluta del archivo de imagen seleccionado por el
     * usuario, o una cadena vacía si no se seleccionó ningún archivo.
     */
    public String buscarImagen() {
        this.rutaImagen = ""; // Inicializa la ruta de la imagen como una cadena vacía
        this.fileChooser = new JFileChooser();

        // Muestra el cuadro de diálogo de selección de archivo
        int result = fileChooser.showOpenDialog(null);

        // Verifica si el usuario seleccionó un archivo
        if (result != JFileChooser.CANCEL_OPTION) {
            this.selectedFile = fileChooser.getSelectedFile();
            this.rutaImagen = selectedFile.getAbsolutePath(); // Obtiene la ruta absoluta del archivo seleccionado
        }
        return this.rutaImagen; // Retorna la ruta de la imagen seleccionada, o una cadena vacía si no se seleccionó ningún archivo
    }

    public void mostrarImagen(String ruta, JLabel label) {
        try {
            BufferedImage imagen = ImageIO.read(new File(rutaImagen));
            this.imageIcon = new ImageIcon(imagen);
            Image scaledImage = this.imageIcon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (IOException e) {
            System.out.println("Error al mostrar imagen: " + e.getMessage());
        }
    }

    public void mostrarImagen(BufferedImage imagenDetectada, JLabel label) {
        try {
            BufferedImage imagen = imagenDetectada;
            this.imageIcon = new ImageIcon(imagen);
            Image scaledImage = this.imageIcon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            System.out.println("Error al mostrar imagen: " + e.getMessage());
        }
    }

}
