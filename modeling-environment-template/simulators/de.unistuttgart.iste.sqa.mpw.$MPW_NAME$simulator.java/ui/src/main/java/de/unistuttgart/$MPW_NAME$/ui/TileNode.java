package de.unistuttgart.$MPW_NAME$.ui;

import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.ViewModelCell;
import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.ViewModelCellLayer;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class TileNode extends StackPane {

    private static final Map<String, Image> images = new HashMap<>();

    static {
        loadCornImages();
    }

    private static void loadCornImages() {
        $FOREACH_IMAGE$
        images.put("$IMAGE_NAME_FIRST_UPPER$", new Image("images/$IMAGE_NAME$.png"));
        $END_FOREACH$
    }

    private final ViewModelCell viewModelCell;
    private final Map<ViewModelCellLayer, ImageView> imageViews = new HashMap<>();

    private final ListChangeListener<ViewModelCellLayer> layerListener = new ListChangeListener<>(){

        @Override
        public void onChanged(final Change<? extends ViewModelCellLayer> change) {
            while(change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(layer -> addLayer(layer));
                }
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(layer -> removeLayer(layer));
                }
            }
        }

    };


    TileNode(final ViewModelCell cell) {
        super();
        this.viewModelCell = cell;

        configureStyle();
        cell.layersProperty().addListener(layerListener);
        cell.layersProperty().forEach(this::addLayer);
    }

    private void configureStyle() {
        this.getStyleClass().add("game-grid-cell");
        var tileImageView = createImageView();
        tileImageView.setImage(images.get("Tile"));
        this.getChildren().add(tileImageView);
    }

    private void addLayer(final ViewModelCellLayer layer) {
        var imageView = createImageView();
        imageView.visibleProperty().bind(layer.visibleProperty());
        imageView.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(layer.getImageName()), layer.imageNameProperty()));
        imageView.rotateProperty().bind(layer.rotationProperty());

        imageViews.put(layer, imageView);
        JavaFXUtil.blockingExecuteOnFXThread(() -> this.getChildren().add(imageView));
    }

    private ImageView createImageView() {
        var imageView = new ImageView();
        imageView.fitHeightProperty().bind(this.heightProperty());
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void removeLayer(final ViewModelCellLayer layer) {
        final ImageView view = imageViews.remove(layer);
        JavaFXUtil.blockingExecuteOnFXThread(() -> this.getChildren().remove(view));
    }

    public void dispose() {
        this.viewModelCell.layersProperty().removeListener(this.layerListener);
    }
}
