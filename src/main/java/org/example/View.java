package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class View {

    private TableView<Persona> tablaPersonas = new TableView<>();
    private ObservableList<Persona> personas = FXCollections.observableArrayList();

    private TextField txtNombre = new TextField();
    private TextField txtDireccion = new TextField();
    private ListView<Telefono> listaTelefonos = new ListView<>();
    private ObservableList<Telefono> telefonosPersonaActual = FXCollections.observableArrayList();
    private TextField txtNuevoTelefono = new TextField();

    private Persona personaSeleccionada = null;
    private PersonaDAO personaDAO = new PersonaDAO();


    public void mostrar(Stage stage) {
        BorderPane root = new BorderPane();

        root.setPadding(new Insets(25));
        root.setLeft(construirTabla());
        root.setCenter(construirFormulario());

        BorderPane.setMargin(
                root.getCenter(),
                new Insets(0, 0, 0, 20)
        );

        cargarPersonas();

        Scene scene = new Scene(root, 900, 550);

        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        stage.setTitle("Agenda - CRUD Personas");
        stage.setScene(scene);
        stage.show();
    }

    private VBox construirTabla() {
        TableColumn<Persona, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(280);

        tablaPersonas.getColumns().add(colNombre);
        tablaPersonas.setItems(personas);
        tablaPersonas.setPrefWidth(300);
        tablaPersonas.setPrefHeight(450);

        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                seleccionarPersona(newVal);
            }
        });

        Label titulo = new Label("Personas registradas");
        titulo.getStyleClass().add("titulo-seccion");

        VBox box = new VBox(15, titulo, tablaPersonas);
        box.getStyleClass().add("panel");
        box.setPrefWidth(330);

        return box;
    }

    private VBox construirFormulario() {
        GridPane form = new GridPane();

        form.setHgap(12);
        form.setVgap(15);

        ColumnConstraints columna1 = new ColumnConstraints();
        columna1.setMinWidth(90);

        ColumnConstraints columna2 = new ColumnConstraints();
        columna2.setHgrow(Priority.ALWAYS);

        form.getColumnConstraints().addAll(columna1, columna2);

        Label lblNombre = new Label("Nombre:");
        Label lblDireccion = new Label("Dirección:");
        Label lblTelefonos = new Label("Teléfonos:");

        form.add(lblNombre, 0, 0);
        form.add(txtNombre, 1, 0);

        form.add(lblDireccion, 0, 1);
        form.add(txtDireccion, 1, 1);

        form.add(lblTelefonos, 0, 2);

        listaTelefonos.setItems(telefonosPersonaActual);
        listaTelefonos.setPrefHeight(120);

        form.add(listaTelefonos, 1, 2);


        Button btnAgregarTel = new Button("Agregar teléfono");
        btnAgregarTel.getStyleClass().add("btn-telefono");
        btnAgregarTel.setOnAction(e -> agregarTelefono());

        txtNuevoTelefono.setPromptText("Número de teléfono");

        HBox telBox = new HBox(8, txtNuevoTelefono, btnAgregarTel);
        HBox.setHgrow(txtNuevoTelefono, Priority.ALWAYS);

        form.add(telBox, 1, 3);


        Button btnGuardar = new Button("Guardar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Nuevo");


        /* Clases para darles colores diferentes */
        btnGuardar.getStyleClass().add("btn-guardar");
        btnActualizar.getStyleClass().add("btn-actualizar");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnLimpiar.getStyleClass().add("btn-nuevo");


        /* Eventos originales */
        btnGuardar.setOnAction(e -> guardar());
        btnActualizar.setOnAction(e -> actualizar());
        btnEliminar.setOnAction(e -> eliminar());
        btnLimpiar.setOnAction(e -> limpiarFormulario());


        HBox botones = new HBox(
                10,
                btnGuardar,
                btnActualizar,
                btnEliminar,
                btnLimpiar
        );

        form.add(botones, 1, 4);


        Label titulo = new Label("Datos de la persona");
        titulo.getStyleClass().add("titulo-seccion");

        VBox box = new VBox(20, titulo, form);
        box.getStyleClass().add("panel");

        return box;
    }

    private void agregarTelefono() {
        String numero = txtNuevoTelefono.getText().trim();
        if (!numero.isEmpty()) {
            telefonosPersonaActual.add(new Telefono(numero));
            txtNuevoTelefono.clear();
        }
    }

    private void seleccionarPersona(Persona p) {
        personaSeleccionada = p;
        txtNombre.setText(p.getNombre());
        txtDireccion.setText(p.getDireccion());
        telefonosPersonaActual.setAll(p.getTelefonos());
    }

    private void limpiarFormulario() {
        personaSeleccionada = null;
        txtNombre.clear();
        txtDireccion.clear();
        telefonosPersonaActual.clear();
        tablaPersonas.getSelectionModel().clearSelection();
    }

    private void cargarPersonas() {
        personas.setAll(personaDAO.listarTodas());
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty()) {
            return;
        }

        Persona p = new Persona(nombre, direccion);
        p.setTelefonos(new ArrayList<>(telefonosPersonaActual));

        personaDAO.insertar(p);
        cargarPersonas();
        limpiarFormulario();
    }

    private void actualizar() {
        if (personaSeleccionada == null) return;

        personaSeleccionada.setNombre(txtNombre.getText().trim());
        personaSeleccionada.setDireccion(txtDireccion.getText().trim());
        personaSeleccionada.setTelefonos(new ArrayList<>(telefonosPersonaActual));

        personaDAO.actualizar(personaSeleccionada);
        cargarPersonas();
        limpiarFormulario();
    }

    private void eliminar() {
        if (personaSeleccionada == null) return;

        personaDAO.eliminar(personaSeleccionada.getId());
        cargarPersonas();
        limpiarFormulario();
    }
}