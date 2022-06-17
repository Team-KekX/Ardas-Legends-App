const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const claimbuild = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        await interaction.reply(`${name} has now settled in ${claimbuild}.`);
    },
};