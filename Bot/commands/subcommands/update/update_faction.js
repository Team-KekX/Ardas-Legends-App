const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {availableFactions} = require("../../../configs/config.json");

module.exports = {
    async execute(interaction) {
        const faction = capitalizeFirstLetters(interaction.options.getString('faction-name').toLowerCase());
        if (!availableFactions.includes(faction)) {
            await interaction.reply({content: `${faction} is not a valid faction.`, ephemeral: true});
            await interaction.followUp({
                content: `Available factions: ${availableFactions.join(', ')}`,
                ephemeral: true
            });
        } else {
            await interaction.reply({
                content: `You were successfully registered as ${faction}.`,
                ephemeral: true
            });
            // send to server
        }
    },
};