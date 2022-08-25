const {capitalizeFirstLetters} = require("../../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");
const {isMemberStaff} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }

        const name = interaction.options.getString('name');
        const executor = interaction.member.id;

        const data = {
            executorDiscordId: executor,
            armyName: name
        }

        axios.delete(`http://${serverIP}:${serverPort}/api/army/delete`, {data: data})
            .then(async function(response) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Staff-Deleted Army or Company")
                    .setColor("GREEN")
                    .setDescription(`${name} has been deleted!`)
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error)  {
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Error while trying to delete ${name}`)
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};
