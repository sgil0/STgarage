package front;

import back.TypeIntervention;
import java.util.List;

public interface SchemaListener {
    // Appelé chaque fois qu'on coche/décoche un truc sur le dessin
    void onSelectionChanged(List<TypeIntervention> selection);
}