package net.amar.oreojava.commands.text.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.DBInserter;
import net.amar.oreojava.db.DBUpdater;
import net.amar.oreojava.db.tables.Data;

import java.sql.SQLException;

public class AddData extends Command {

    public AddData() {
        this.name = "data";
        this.help = "add a new value to the Data table";
        this.arguments = "[fieldName] [fieldValue]";
        this.aliases = new String[]{"add"};
        this.ownerCommand = true;
        this.category = Categories.owner;
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+",2);
        if (args.length < 1) {
            event.replyError("Please provide all arguments " + this.arguments);
            return;
        }
        String fieldName = args[0];
        String fieldValue = args[1];

        if (DBGetter.dataAlreadyExists(fieldName, Oreo.getConnection())) {
            try {
                DBUpdater.updateData(Oreo.getConnection(), new Data(fieldName, fieldValue));
                event.replySuccess("Updated *"+fieldName+"* to ["+fieldValue+"] :3");
            } catch (SQLException e) {
                event.replyError("Failed to update value: ["+e.getMessage()+"]");
            }
            return;
        }

        try {
            DBInserter.insert(Oreo.getConnection(), new Data(fieldName, fieldValue));
            event.replySuccess("Added *"+fieldName+"* field with value ["+fieldValue+"]");
        } catch (SQLException e) {
            event.replyError("Failed to add value: ["+e.getMessage()+"]");
            Log.error("Failed to add new value to data table",e);
        }
    }
}
