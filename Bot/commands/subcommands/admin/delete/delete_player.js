const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const discordId = capitalizeFirstLetters(interaction.options.getString('discord-id').toLowerCase());
        // send to server
        await interaction.reply({content: `Deleted player with discord ID: ${discordId}`});
    }
};