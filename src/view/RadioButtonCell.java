package view;

import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.util.EnumSet;

public class RadioButtonCell<S,T extends Enum<T>> extends TableCell<S,T> {

    private final EnumSet<T> enumeration;

    public RadioButtonCell(EnumSet<T> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    protected void updateItem(T item, boolean empty)
    {
        super.updateItem(item, empty);
        setGraphic(null);
        if (!empty)
        {
            // gui setup
            HBox hb = new HBox(7);
            hb.setAlignment(Pos.CENTER);
            final ToggleGroup group = new ToggleGroup();

            // create a radio button for each 'element' of the enumeration
            for (Enum<T> enumElement : enumeration) {
                JFXRadioButton radioButton = new JFXRadioButton(enumElement.toString());
                radioButton.setUserData(enumElement);
                radioButton.setToggleGroup(group);
                hb.getChildren().add(radioButton);
                if (enumElement.equals(item)) {
                    radioButton.setSelected(true);
                }
            }

            // issue events on change of the selected radio button
            group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue,
                                                        Toggle newValue) -> {
                getTableView().edit(getIndex(), getTableColumn());
                //noinspection unchecked
                RadioButtonCell.this.commitEdit((T) newValue.getUserData());
            });
            setGraphic(hb);
        }
    }
}