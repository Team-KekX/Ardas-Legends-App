
module.exports = {
    // TO BE EXPANDED
    async execute(interaction) {
        let name=interaction.options.getString('army-name').toLowerCase();
        let food=interaction.options.getString('food-type').toLowerCase();
        const start=interaction.options.getInteger('start-region');
        const destination=interaction.options.getInteger('destination-region');

        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = name.split(" ");
        const arr_food = food.split(" ");

        //loop through each element of the array and capitalize the first letter.


        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }
        for (let i = 0; i < arr_food.length; i++) {
            arr_food[i] = arr_food[i].charAt(0).toUpperCase() + arr_food[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        food = arr_food.join(" ");
        await interaction.deferReply();
        await interaction.editReply(`${name} moved from ${start} to ${destination}, using ${food} for payment.`);
    },
};