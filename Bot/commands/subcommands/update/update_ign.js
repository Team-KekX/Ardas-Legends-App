const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const ign = capitalizeFirstLetters(interaction.options.getString('ign').toLowerCase());
        await interaction.deferReply();
        // send to server and edit reply
    },
};