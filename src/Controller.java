import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Controller {

    public TextArea taCodeField;
    public Button btnCount;
    public Button btnLoadFromFile;
    public TableView<TableViewElement> tOperators;
    public TableColumn<TableViewElement, Integer> opDepth;
    public TableColumn<TableViewElement, String> operator;
    public TextField relativeEnclosure;
    public TextField maximumEnclosure;
    public TextField absoluteEnclosure;
    public TextField operators_count;

    @FXML
    private void btnLoadFromFileOnClick(MouseEvent e) {
        File selectedFile = dlgToOpenFile(false);

        if (selectedFile == null) return;
        Charset charset = StandardCharsets.UTF_8;
        StringBuilder fileInformation = new StringBuilder();
        try(BufferedReader reader = Files.newBufferedReader(selectedFile.toPath(), charset)) {
            String line;
            while ((line = reader.readLine()) != null) fileInformation.append(line).append('\n');
            taCodeField.setText(fileInformation.toString());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    private void saveToFile(){
        Charset charset = StandardCharsets.UTF_8;
        try(BufferedWriter writer = Files.newBufferedWriter(new File(PythonJilbMetrics.pathToInputFile).toPath(), charset)) {
            writer.write(taCodeField.getText());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    private File dlgToOpenFile(boolean isSave){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(PythonJilbMetrics.defaultDirToFile));
        fileChooser.setTitle("Выберите файл");
        return isSave ? fileChooser.showSaveDialog(View.Interface) : fileChooser.showOpenDialog(View.Interface);
    }

    @FXML
    public void btnProcess(MouseEvent mouseEvent){
        saveToFile();
        PythonJilbMetrics.parse();
        int CL = PythonJilbMetrics.countAbsoluteEnclosure();
        float cl = PythonJilbMetrics.countRelativeEnclosure(CL);
        int max = PythonJilbMetrics.findMaximumEnclosure();

        ObservableList<TableViewElement> operatorsData = FXCollections.observableArrayList();

        PythonJilbMetrics.operators_flow.forEach(item->operatorsData.add(new TableViewElement(item.statement(),item.depth())));
        opDepth.setCellValueFactory(new PropertyValueFactory<TableViewElement, Integer>("depth"));
        operator.setCellValueFactory(new PropertyValueFactory<TableViewElement, String>("statement"));

        tOperators.setItems(operatorsData);

        operators_count.setText(String.valueOf(PythonJilbMetrics.operators_flow.size()));
        absoluteEnclosure.setText(String.valueOf(CL));
        relativeEnclosure.setText(String.format("%.3f",cl));
        maximumEnclosure.setText(String.valueOf(max));
    }
    @FXML

    public static void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setContentText(message);
        alert.show();

    }
}
