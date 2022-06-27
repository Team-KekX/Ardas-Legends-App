const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('armed-company-name').toLowerCase());
        await interaction.reply(`The armed company \"${name}\" has been disbanded. Now the army and traders are
            separated`);
    },
};