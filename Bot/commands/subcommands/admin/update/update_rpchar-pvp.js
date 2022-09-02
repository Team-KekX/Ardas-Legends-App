const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../../configs/config.json");
const {UPDATE} = require('../../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }
        const pvp = interaction.options.getBoolean('new-pvp');

        //data sent to server
        const data = {
            discordId: interaction.options.getString('discord-id'),
            pvp: pvp
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/rpchar/pvp', data)
            .then(async function() {

                //used to build the description later on
                let description = 'no longer';
                if(pvp)
                    description = 'now';

                //if request successful
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update RpChar PvP`)
                    .setColor('GREEN')
                    .setDescription(`Your Roleplay Character will ${description} participate in PvP!`)
                    .setThumbnail(UPDATE)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while updating roleplay character pvp")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })

    },
};