const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');
const {CREATE} = require('../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('armed-company-name').toLowerCase());
        const army=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const trader=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const character=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Create armed company`)
                                .setColor('RED')
                                .setDescription(`The armed company ${name} comprised of the army ${army} and trading company ${trader},
                                has been created and bound to ${character}.`)
                                .setThumbnail(CREATE)
                                .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};