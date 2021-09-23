package de.unistuttgart.$MPW_NAME$.facade;

import de.unistuttgart.iste.sqa.mpw.framework.datatypes.Direction;
import de.unistuttgart.iste.sqa.mpw.framework.datatypes.Location;
import de.unistuttgart.iste.sqa.mpw.framework.datatypes.Size;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class $STAGE_NAME_FIRST_UPPER$Loader {
    private final $STAGE_NAME_FIRST_UPPER$Builder $STAGE_NAME$Builder;
    private Size loaded$STAGE_NAME_FIRST_UPPER$Dimensions;

    private $STAGE_NAME_FIRST_UPPER$Loader(final $STAGE_NAME_FIRST_UPPER$Builder $STAGE_NAME$Builder) {
        super();
        this.$STAGE_NAME$Builder = $STAGE_NAME$Builder;
    }

    public static $STAGE_NAME_FIRST_UPPER$Loader initializeFor(final $MPW_NAME_FIRST_UPPER$Game game) {
        var builder = new $STAGE_NAME_FIRST_UPPER$Builder(game);
        return new $STAGE_NAME_FIRST_UPPER$Loader(builder);
    }

    public void loadFromResourceFile(final String $STAGE_NAME$File) throws IOException {
        final List<String> list = readLinesFrom$STAGE_NAME_FIRST_UPPER$ResourceFile($STAGE_NAME$File);
        interpretLoaded$STAGE_NAME_FIRST_UPPER$Lines(list);
    }

    public void loadFromInputStream(final InputStream inputStream) throws IOException {
        final List<String> list = readLinesFrom$STAGE_NAME_FIRST_UPPER$InputStream(inputStream);
        interpretLoaded$STAGE_NAME_FIRST_UPPER$Lines(list);
    }

    private void interpretLoaded$STAGE_NAME_FIRST_UPPER$Lines(final List<String> list) {
        final String[] lines = list.toArray(new String[]{});
        setSizeFromStrings(lines);
        final String[] $STAGE_NAME$Definition = Arrays.copyOfRange(lines,2,lines.length);
        buildTiles($STAGE_NAME$Definition);
    }

    private void setSizeFromStrings(final String[] lines) {
        this.loaded$STAGE_NAME_FIRST_UPPER$Dimensions = new Size(Integer.parseInt(lines[0]), Integer.parseInt(lines[1]));
        this.$STAGE_NAME$Builder.init$STAGE_NAME_FIRST_UPPER$(this.loaded$STAGE_NAME_FIRST_UPPER$Dimensions.getColumnCount(), this.loaded$STAGE_NAME_FIRST_UPPER$Dimensions.getRowCount()); // todo allow size as parameter
    }

    private void buildTiles(final String[] lines) {
        for (int row = 0; row < this.loaded$STAGE_NAME_FIRST_UPPER$Dimensions.getRowCount(); row++) {
            for (int column = 0; column < this.loaded$STAGE_NAME_FIRST_UPPER$Dimensions.getColumnCount(); column++) {
                final Location currentLocation = new Location(column, row);
                final char tileCode = lines[row].charAt(column);
                switch (tileCode) {
                    case ' ':
                        break;
                    // TODO: custom loader code
                    default:
                        throw new RuntimeException("$STAGE_NAME_FIRST_UPPER$ loader error.");
                }
            }
        }
    }

    private List<String> readLinesFrom$STAGE_NAME_FIRST_UPPER$ResourceFile(final String $STAGE_NAME$FileName) throws IOException {
        final InputStream in = getClass().getResourceAsStream($STAGE_NAME$FileName);
        if (in == null) {
            throw new IOException("Unable to load the $STAGE_NAME$ from the filename: " + $STAGE_NAME$FileName);
        }
        final List<String> result = readLinesFrom$STAGE_NAME_FIRST_UPPER$InputStream(in);
        in.close();
        return result;
    }

    private List<String> readLinesFrom$STAGE_NAME_FIRST_UPPER$InputStream(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> list = new ArrayList<String>();

        try (Scanner input = new Scanner(reader))
        {
            while (input.hasNextLine()) {
                list.add(input.nextLine());
            }
        }

        return list;
    }

    private void checkNotNull(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
    }

}
