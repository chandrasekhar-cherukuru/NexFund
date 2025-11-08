import React, { useState } from 'react';
import { Star, Send, X } from 'lucide-react';
import toast from 'react-hot-toast';
import './Feedback.css';

const Feedback = ({ isOpen, onClose, isDarkMode }) => {
  const [feedbackData, setFeedbackData] = useState({
    name: '',
    email: '',
    message: '',
    type: 'event',
    rating: 5,
  });

  const [isSubmittingFeedback, setIsSubmittingFeedback] = useState(false);

  const handleFeedbackChange = (e) => {
    const { name, value } = e.target;
    setFeedbackData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFeedbackRating = (rating) => {
    setFeedbackData((prev) => ({ ...prev, rating }));
  };

  const handleFeedbackSubmit = async (e) => {
    e.preventDefault();
    setIsSubmittingFeedback(true);

    try {
      const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/feedback`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(feedbackData),
      });

      const data = await response.json();

      if (response.ok) {
        toast.success('Thank you! Your feedback has been submitted! 🙏');
        onClose();
        setFeedbackData({ name: '', email: '', message: '', type: 'event', rating: 5 });
      } else {
        toast.error(data.error || 'Failed to submit feedback');
      }
    } catch (error) {
      console.error('❌ Error submitting feedback:', error);
      toast.error('Error submitting feedback');
    } finally {
      setIsSubmittingFeedback(false);
    }
  };

  const handleSkipFeedback = () => {
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className={`rounded-xl shadow-2xl w-full max-w-md overflow-hidden ${
        isDarkMode 
          ? 'bg-gray-800' 
          : 'bg-white'
      }`}>
        <div className={`flex justify-between items-center p-6 border-b ${
          isDarkMode
            ? 'border-gray-700 bg-gray-700'
            : 'border-gray-200 bg-gradient-to-r from-blue-50 to-purple-50'
        }`}>
          <h2 className={`text-2xl font-bold ${
            isDarkMode 
              ? 'text-white' 
              : 'text-gray-900'
          }`}>
            Share Your Feedback
          </h2>
          <button
            onClick={handleSkipFeedback}
            className={`p-1 rounded-lg transition-colors ${
              isDarkMode
                ? 'hover:bg-gray-600'
                : 'hover:bg-white/50'
            }`}
          >
            <X className={`h-5 w-5 ${
              isDarkMode
                ? 'text-gray-400'
                : 'text-gray-600'
            }`} />
          </button>
        </div>

        <form onSubmit={handleFeedbackSubmit} className="p-6 space-y-4">
          <div>
            <label className={`block text-sm font-medium mb-2 ${
              isDarkMode
                ? 'text-gray-300'
                : 'text-gray-700'
            }`}>
              Name *
            </label>
            <input
              type="text"
              name="name"
              value={feedbackData.name}
              onChange={handleFeedbackChange}
              required
              className={`w-full px-3 py-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 border ${
                isDarkMode
                  ? 'bg-gray-700 text-white border-gray-600'
                  : 'bg-white text-gray-900 border-gray-300'
              }`}
              placeholder="Your name"
            />
          </div>

          <div>
            <label className={`block text-sm font-medium mb-2 ${
              isDarkMode
                ? 'text-gray-300'
                : 'text-gray-700'
            }`}>
              Email *
            </label>
            <input
              type="email"
              name="email"
              value={feedbackData.email}
              onChange={handleFeedbackChange}
              required
              className={`w-full px-3 py-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 border ${
                isDarkMode
                  ? 'bg-gray-700 text-white border-gray-600'
                  : 'bg-white text-gray-900 border-gray-300'
              }`}
              placeholder="Your email"
            />
          </div>

          <div>
            <label className={`block text-sm font-medium mb-2 ${
              isDarkMode
                ? 'text-gray-300'
                : 'text-gray-700'
            }`}>
              Type of Feedback *
            </label>
            <select
              name="type"
              value={feedbackData.type}
              onChange={handleFeedbackChange}
              required
              className={`w-full px-3 py-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 border ${
                isDarkMode
                  ? 'bg-gray-700 text-white border-gray-600'
                  : 'bg-white text-gray-900 border-gray-300'
              }`}
            >
              <option value="event">Event</option>
              <option value="donation">Donation</option>
              <option value="giftpool">Gift Pool</option>
            </select>
          </div>

          <div>
            <label className={`block text-sm font-medium mb-2 ${
              isDarkMode
                ? 'text-gray-300'
                : 'text-gray-700'
            }`}>
              Rating *
            </label>
            <div className="flex gap-2">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  onClick={() => handleFeedbackRating(star)}
                  className={`transition-all ${
                    feedbackData.rating >= star
                      ? 'text-yellow-400'
                      : isDarkMode
                      ? 'text-gray-600'
                      : 'text-gray-300'
                  }`}
                >
                  <Star className="h-6 w-6 fill-current" />
                </button>
              ))}
              <span className={`ml-2 ${
                isDarkMode
                  ? 'text-gray-400'
                  : 'text-gray-600'
              }`}>
                {feedbackData.rating} / 5
              </span>
            </div>
          </div>

          <div>
            <label className={`block text-sm font-medium mb-2 ${
              isDarkMode
                ? 'text-gray-300'
                : 'text-gray-700'
            }`}>
              Your Feedback *
            </label>
            <textarea
              name="message"
              value={feedbackData.message}
              onChange={handleFeedbackChange}
              required
              rows="4"
              className={`w-full px-3 py-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 border resize-none ${
                isDarkMode
                  ? 'bg-gray-700 text-white border-gray-600'
                  : 'bg-white text-gray-900 border-gray-300'
              }`}
              placeholder="Tell us what you think..."
            />
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={handleSkipFeedback}
              className={`flex-1 px-4 py-2 rounded-lg transition-colors font-medium ${
                isDarkMode
                  ? 'bg-gray-600 text-white hover:bg-gray-500'
                  : 'bg-gray-300 text-gray-900 hover:bg-gray-400'
              }`}
            >
              Skip
            </button>
            <button
              type="submit"
              disabled={isSubmittingFeedback}
              className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors font-medium disabled:opacity-50"
            >
              {isSubmittingFeedback ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  <span>Submitting...</span>
                </>
              ) : (
                <>
                  <Send className="h-4 w-4" />
                  <span>Submit</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Feedback;
