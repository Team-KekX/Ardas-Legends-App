const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");


module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const characterName = capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        // send to server
        await interaction.reply({content: `Deleted character ${characterName}`});
    },
};

