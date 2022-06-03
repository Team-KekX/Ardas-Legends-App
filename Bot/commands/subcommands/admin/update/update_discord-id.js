const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const oldId = capitalizeFirstLetters(interaction.options.getString('old-discord-id').toLowerCase());
        const newId = capitalizeFirstLetters(interaction.options.getString('new-discord-id').toLowerCase());
        // send to server
        await interaction.reply({content: `Updated discord ID of player from ${oldId} to ${newId}`});
    },
};
